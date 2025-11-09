package samukadev.coderpg.security.utils;

import lombok.experimental.UtilityClass;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.security.oauth.GitHubOAuthConfig;

import java.util.Map;

@UtilityClass
public class RefreshTokenFromGitHub {

    public static Map execute(String refreshToken, GitHubOAuthConfig oauthConfig, WebClient.Builder webClientBuilder) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri("https://github.com/login/oauth/access_token")
                    .bodyValue(Map.of(
                            "client_id", oauthConfig.getClientId(),
                            "client_secret", oauthConfig.getClientSecret(),
                            "grant_type", "refresh_token",
                            "refresh_token", refreshToken
                    ))
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(Map.class)
                    .onErrorResume(error -> {
                        return Mono.error(new BusinessException("GitHub token refresh failed: " + error.getMessage()));
                    })
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Failed to refresh GitHub token: " + e.getMessage());
        }
    }

}
