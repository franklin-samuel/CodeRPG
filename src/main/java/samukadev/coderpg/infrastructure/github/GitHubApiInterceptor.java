package samukadev.coderpg.infrastructure.github;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.exceptions.GitHubApiException;
import samukadev.coderpg.security.oauth.GitHubOAuthService;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class GitHubApiInterceptor implements ClientHttpRequestInterceptor {

    private final GitHubOAuthService gitHubOAuthService;
    private static final ThreadLocal<UUID> CURRENT_USER_ID = new ThreadLocal<>();

    public static void setCurrentUserId(UUID userId) {
        CURRENT_USER_ID.set(userId);
    }

    public static void clearCurrentUserId() {
        CURRENT_USER_ID.remove();
    }

    @Override
    public ClientHttpResponse intercept(
            HttpRequest request,
            byte[] body,
            ClientHttpRequestExecution execution
    ) throws IOException {

        UUID userId = CURRENT_USER_ID.get();

        if (userId != null) {
            try {
                String accessToken = gitHubOAuthService.getAccessToken(userId);

                if(accessToken != null && !accessToken.isBlank()) {
                    request.getHeaders().setBearerAuth(accessToken);
                }
            } catch (Exception e) {
                throw new GitHubApiException("Failed to authenticate with Github", 401);
            }
        }

        request.getHeaders().set("Accept", "application/vnd.github.v3+json");
        request.getHeaders().set("User-Agent", "CodeRPG-App");

        ClientHttpResponse response = execution.execute(request, body);

        String rateLimitRemaining = response.getHeaders().getFirst("X-RateLimit-Remaining");
        String rateLimitReset = response.getHeaders().getFirst("X-RateLimit-Reset");

        if (rateLimitRemaining != null) {
            log.debug("GitHub API Rate Limit - Remaining: {}, Reset: {}", rateLimitRemaining, rateLimitReset);
        }

        if (response.getStatusCode().value() == 403) {
            String rateLimitMessage = response.getHeaders().getFirst("X-RateLimit-Remaining");
            if ("0".equals(rateLimitRemaining)) {
                throw new GitHubApiException("GitHub API Rate Limit exceeded", 429);
            }
        }

        return response;

    }

}
