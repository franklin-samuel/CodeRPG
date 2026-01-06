package samukadev.coderpg.web.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.CreateUserPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.core.security.RevokeGitHubTokenPort;
import samukadev.coderpg.core.security.SaveGitHubTokenPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.security.oauth.GitHubMobileAuthService;
import samukadev.coderpg.security.oauth.GitHubOAuthService;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.model.request.MobileAuthRequest;
import samukadev.coderpg.web.model.response.AuthStatusResponse;
import samukadev.coderpg.web.routes.AuthRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthController {

    private final GitHubMobileAuthService mobileAuthService;
    private final SecurityUtils securityUtils;
    private final RevokeGitHubTokenPort revokeGitHubTokenPort;
    private final GitHubOAuthService gitHubOAuthService;
    private final UserRepositoryPort userRepository;
    private final CreateUserPort createUserPort;
    private final SaveGitHubTokenPort saveGitHubTokenPort;

    @PostMapping(AuthRoute.LOGIN)
    public ResponseEntity<ApiResponse<AuthStatusResponse>> authenticateWithGithub(
            @Valid @RequestBody MobileAuthRequest request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
    ) {
        Map<String, Object> tokenResponse = mobileAuthService
                .exchangeCodeForToken(request.getCode(), request.getRedirectUri());

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException("Failed to get access token from github");
        }

        Map<String, Object> githubUser = mobileAuthService.getGitHubUser(accessToken);

        Long githubId = ((Number) githubUser.get("id")).longValue();
        String githubUsername = (String) githubUser.get("login");
        String email = (String) githubUser.get("email");
        String avatarUrl = (String) githubUser.get("avatar_url");
        String bio = (String) githubUser.get("bio");
        String location = (String) githubUser.get("location");
        String website = (String) githubUser.get("blog");
        Integer publicRepos = (Integer) githubUser.get("public_repos");
        Integer followers = (Integer) githubUser.get("followers");
        Integer following = (Integer) githubUser.get("following");
        String createdAt = (String) githubUser.get("created_at");

        Optional<User> existingUser = userRepository.findByGitHubId(githubId);
        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setGithubUsername(githubUsername);
            user.setAvatarUrl(avatarUrl);
            user.setBio(bio);
            user.setLocation(location);
            user.setWebsite(website);
            user.setGithubPublicRepos(publicRepos);
            user.setGithubFollowers(followers);
            user.setGithubFollowing(following);
            user.setLastSyncAt(LocalDateTime.now());
            user = userRepository.save(user);
        } else {
            isNewUser = true;
            User newUser = User.builder()
                    .githubId(githubId)
                    .githubUsername(githubUsername)
                    .name(null)
                    .email(email)
                    .avatarUrl(avatarUrl)
                    .bio(bio)
                    .location(location)
                    .website(website)
                    .classType(null)
                    .githubPublicRepos(publicRepos)
                    .githubFollowers(followers)
                    .githubFollowing(following)
                    .githubCreatedAt(createdAt != null ?
                            LocalDateTime.parse(createdAt.replace("Z", "")) : null)
                    .active(true)
                    .build();

            Context createUserContext = new Context(newUser);
            user = createUserPort.execute(createUserContext);
        }

        LocalDateTime expiresAt = mobileAuthService.calculateExpiresAt(tokenResponse.get("expires_in"));

        Context saveTokenContext = new Context();
        saveTokenContext.putProperty("userId", user.getId());
        saveTokenContext.putProperty("accessToken", accessToken);
        saveTokenContext.putProperty("refreshToken", refreshToken);
        saveTokenContext.putProperty("expiresAt", expiresAt);
        saveGitHubTokenPort.execute(saveTokenContext);

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                githubId.toString(),
                null,
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"))
        );

        SecurityContext securityContext = SecurityContextHolder.createEmptyContext();
        securityContext.setAuthentication(authentication);
        SecurityContextHolder.setContext(securityContext);

        httpRequest.getSession().setAttribute(
                HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                securityContext
        );

        Cookie sessionCookie = new Cookie("JSESSIONID", httpRequest.getSession().getId());
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(true);
        sessionCookie.setPath("/");
        sessionCookie.setMaxAge(60 * 60 * 24 * 7);
        httpResponse.addCookie(sessionCookie);

        boolean needsOnBoarding = user.getName() == null || user.getClassType() == null;

        AuthStatusResponse authStatus = AuthStatusResponse.builder()
                .authenticated(true)
                .userId(user.getId())
                .githubUsername(user.getGithubUsername())
                .name(user.getName())
                .avatarUrl(user.getAvatarUrl())
                .hasValidGitHubToken(true)
                .needsOnBoarding(needsOnBoarding)
                .build();
        return ResponseEntity
                .status(isNewUser ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.success(authStatus));
    }

    @GetMapping(AuthRoute.STATUS)
    public ResponseEntity<ApiResponse<AuthStatusResponse>> getAuthStatus(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        log.debug("Auth status check - Principal present: {}", principal != null);

        if (principal == null) {
            log.debug("No authentication principal found");
            AuthStatusResponse response = AuthStatusResponse.builder()
                    .authenticated(false)
                    .build();
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        try {
            log.debug("Principal attributes: {}", principal.getAttributes());

            User user = securityUtils.getAuthenticatedUser(principal);
            boolean hasValidToken = gitHubOAuthService.hasValidToken(user.getId());

            log.info("User authenticated: {} ({})", user.getGithubUsername(), user.getId());

            AuthStatusResponse response = AuthStatusResponse.builder()
                    .authenticated(true)
                    .userId(user.getId())
                    .githubUsername(user.getGithubUsername())
                    .name(user.getName())
                    .avatarUrl(user.getAvatarUrl())
                    .hasValidGitHubToken(hasValidToken)
                    .needsOnBoarding(user.getName() == null || user.getClassType() == null)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BusinessException e) {
            log.error("Error getting authenticated user: {}!", e.getMessage());
            AuthStatusResponse response = AuthStatusResponse.builder()
                    .authenticated(false)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        }
    }

    @PostMapping(AuthRoute.LOGOUT)
    public ResponseEntity<ApiResponse<String>> logout(
            @AuthenticationPrincipal OAuth2User principal,
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        if (principal != null) {
            try {
                UUID userId = securityUtils.getAuthenticatedUserId(principal);

                Context context = new Context();
                context.putProperty("userId", userId);
                revokeGitHubTokenPort.execute(context);

            } catch (BusinessException e) {
                log.error("Error during logout: {}", e.getMessage());
            }
        }

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully!"));
    }

    @PostMapping(AuthRoute.REFRESH)
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        try {
            gitHubOAuthService.refreshAccessToken(userId);
            return ResponseEntity.ok(ApiResponse.success("Refreshed successfully"));
        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "REFRESH_FAILED"));
        }
    }
}