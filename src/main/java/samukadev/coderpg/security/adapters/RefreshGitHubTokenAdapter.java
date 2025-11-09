package samukadev.coderpg.security.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.persistence.GitHubTokenRepositoryPort;
import samukadev.coderpg.core.security.RefreshGitHubTokenPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.security.oauth.GitHubOAuthConfig;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshGitHubTokenAdapter implements RefreshGitHubTokenPort {

    private final GitHubTokenRepositoryPort tokenRepository;
    private final GitHubOAuthConfig oauthConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    public GitHubToken execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);

        if (userId == null) {
            throw new BusinessException("User ID is required");
        }

        GitHubToken currentToken = tokenRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new BusinessException("No token found for refresh"));

        if (currentToken.getGithubRefreshToken() == null) {
            throw new BusinessException("No refresh token available. Please login again.");
        }

        Map<String, String> refreshResponse = refreshTokenFromGitHub(currentToken.getGithubRefreshToken());

        String newAccessToken = refreshResponse.get("access_token");
        String newRefreshToken = refreshResponse.get("refresh_token");
        int expiresIn = Integer.parseInt(refreshResponse.getOrDefault("expires_in", "28800"));

        if (newAccessToken == null) {
            throw new BusinessException("Failed to refresh GitHub token");
        }

        currentToken.setActive(false);
        tokenRepository.save(currentToken);

        GitHubToken newToken = GitHubToken.builder()
                .userId(userId)
                .githubAccessToken(newAccessToken)
                .githubRefreshToken(newRefreshToken != null ? newRefreshToken : currentToken.getGithubRefreshToken())
                .expiresAt(LocalDateTime.now().plusSeconds(expiresIn))
                .active(true)
                .build();

        return tokenRepository.save(newToken);
    }

    private Map<String, String> refreshTokenFromGitHub(String refreshToken) {
        try {
            return webClientBuilder.build()
                    .post()
                    .uri("https://github.com/login/oauth/access_token")
                    .body(BodyInserters.fromFormData("client_id", oauthConfig.getClientId())
                            .with("client_secret", oauthConfig.getClientSecret())
                            .with("grant_type", "refresh_token")
                            .with("refresh_token", refreshToken))
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, String>>() {})
                    .onErrorResume(error -> Mono.error(new BusinessException(
                            "GitHub token refresh failed: " + error.getMessage()
                    )))
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Failed to refresh GitHub token: " + e.getMessage());
        }
    }

}
