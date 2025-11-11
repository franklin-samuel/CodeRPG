package samukadev.coderpg.web.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.security.RevokeGitHubTokenPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.security.oauth.GitHubOAuthService;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.model.response.AuthStatusResponse;
import samukadev.coderpg.web.routes.AuthRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final SecurityUtils securityUtils;
    private final RevokeGitHubTokenPort revokeGitHubTokenPort;
    private final GitHubOAuthService gitHubOAuthService;

    @GetMapping(AuthRoute.STATUS)
    public ResponseEntity<ApiResponse<AuthStatusResponse>> getAuthStatus(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        if (principal == null) {
            AuthStatusResponse response = AuthStatusResponse.builder()
                    .authenticated(false)
                    .build();
            return ResponseEntity.ok(ApiResponse.success(response));
        }

        try {
            User user = securityUtils.getAuthenticatedUser(principal);
            boolean hasValidToken = gitHubOAuthService.hasValidToken(user.getId());

            AuthStatusResponse response = AuthStatusResponse.builder()
                    .authenticated(true)
                    .userId(user.getId())
                    .githubUsername(user.getGithubUsername())
                    .avatarUrl(user.getAvatarUrl())
                    .hasValidGitHubToken(hasValidToken)
                    .needsOnBoarding(user.getName() == null || user.getClassType() == null)
                    .build();

            return ResponseEntity.ok(ApiResponse.success(response));
        } catch (BusinessException e) {
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
                throw new BusinessException(e.getMessage());
            }
        }

        new SecurityContextLogoutHandler()
                .logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return ResponseEntity.ok(ApiResponse.success("Logged out successfully"));
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
