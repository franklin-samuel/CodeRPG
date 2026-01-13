package samukadev.coderpg.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
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
import samukadev.coderpg.web.model.response.LoginResponse;
import samukadev.coderpg.web.routes.AuthRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.time.LocalDateTime;
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
    public ResponseEntity<ApiResponse<LoginResponse>> authenticateWithGithub(
            @Valid @RequestBody MobileAuthRequest request
    ) {
        log.info("Mobile login attempt - redirectUri: {}", request.getRedirectUri());

        Map<String, Object> tokenResponse = mobileAuthService
                .exchangeCodeForToken(request.getCode(), request.getRedirectUri());

        String accessToken = (String) tokenResponse.get("access_token");
        String refreshToken = (String) tokenResponse.get("refresh_token");

        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException("Failed to get access token from GitHub");
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

            log.info("Existing user logged in: {} ({})", user.getGithubUsername(), user.getId());
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

            log.info("New user created: {} ({})", user.getGithubUsername(), user.getId());
        }

        LocalDateTime expiresAt = mobileAuthService.calculateExpiresAt(tokenResponse.get("expires_in"));

        Context saveTokenContext = new Context();
        saveTokenContext.putProperty("userId", user.getId());
        saveTokenContext.putProperty("accessToken", accessToken);
        saveTokenContext.putProperty("refreshToken", refreshToken);
        saveTokenContext.putProperty("expiresAt", expiresAt);
        saveGitHubTokenPort.execute(saveTokenContext);

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

        LoginResponse loginResponse = LoginResponse.builder()
                .token(user.getId().toString())
                .authStatus(authStatus)
                .build();

        return ResponseEntity
                .status(isNewUser ? HttpStatus.CREATED : HttpStatus.OK)
                .body(ApiResponse.success(loginResponse));
    }

    @GetMapping(AuthRoute.STATUS)
    public ResponseEntity<ApiResponse<AuthStatusResponse>> getAuthStatus(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        log.debug("Auth status check - Authorization header present: {}", authHeader != null);

        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {

            log.debug("No authentication found in context");
            return ResponseEntity.ok(ApiResponse.success(
                    AuthStatusResponse.builder().authenticated(false).build()
            ));
        }

        try {
            String githubIdStr = authentication.getName();
            Long githubId = Long.parseLong(githubIdStr);

            Optional<User> userOpt = userRepository.findByGitHubId(githubId);

            if (userOpt.isEmpty()) {
                log.warn("User not found for GitHub ID: {}", githubId);
                return ResponseEntity.ok(ApiResponse.success(
                        AuthStatusResponse.builder().authenticated(false).build()
                ));
            }

            User user = userOpt.get();
            boolean hasValidToken = gitHubOAuthService.hasValidToken(user.getId());

            log.info("User status checked: {} ({})", user.getGithubUsername(), user.getId());

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

        } catch (Exception e) {
            log.error("Error getting auth status: {}", e.getMessage());
            return ResponseEntity.ok(ApiResponse.success(
                    AuthStatusResponse.builder().authenticated(false).build()
            ));
        }
    }

    @PostMapping(AuthRoute.LOGOUT)
    public ResponseEntity<ApiResponse<String>> logout(
            @RequestHeader(value = "Authorization", required = false) String authHeader,
            HttpServletRequest request
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated()) {
            try {
                String githubIdStr = authentication.getName();
                Long githubId = Long.parseLong(githubIdStr);

                Optional<User> userOpt = userRepository.findByGitHubId(githubId);

                if (userOpt.isPresent()) {
                    Context context = new Context();
                    context.putProperty("userId", userOpt.get().getId());
                    revokeGitHubTokenPort.execute(context);

                    log.info("User {} logged out successfully", userOpt.get().getGithubUsername());
                }
            } catch (Exception e) {
                log.error("Error during logout: {}", e.getMessage());
            }
        }

        new SecurityContextLogoutHandler().logout(request, null, authentication);
        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully!"));
    }

    @PostMapping(AuthRoute.REFRESH)
    public ResponseEntity<ApiResponse<String>> refreshToken(
            @RequestHeader(value = "Authorization", required = false) String authHeader
    ) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ApiResponse.error("Not authenticated", "NOT_AUTHENTICATED"));
        }

        try {
            String githubIdStr = authentication.getName();
            Long githubId = Long.parseLong(githubIdStr);

            Optional<User> userOpt = userRepository.findByGitHubId(githubId);

            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(ApiResponse.error("User not found", "USER_NOT_FOUND"));
            }

            gitHubOAuthService.refreshAccessToken(userOpt.get().getId());
            return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully"));

        } catch (BusinessException e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage(), "REFRESH_FAILED"));
        }
    }
}