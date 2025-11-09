package samukadev.coderpg.security.adapters;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.security.RevokeGitHubTokenPort;
import samukadev.coderpg.core.persistence.GitHubTokenRepositoryPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.security.oauth.GitHubOAuthConfig;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class RevokeGitHubTokenAdapter implements RevokeGitHubTokenPort {

    private final GitHubTokenRepositoryPort tokenRepository;
    private final GitHubOAuthConfig oauthConfig;
    private final WebClient.Builder webClientBuilder;

    @Override
    public GitHubToken execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);

        if (userId == null) {
            throw new BusinessException("User ID is required");
        }

        GitHubToken token = tokenRepository.findLatestByUserId(userId)
                .orElseThrow(() -> new BusinessException("No token found to revoke"));

        try {
            revokeTokenFromGitHub(token.getGithubAccessToken());
        } catch (Exception e) {
            log.warn("Failed to revoke token on GitHub (continuing anyway): {}", e.getMessage());
        }

        token.setActive(false);
        GitHubToken revokedToken = tokenRepository.save(token);

        log.info("Revoked GitHub token for user: {}", userId);
        context.putProperty("tokenRevoked", true);

        return revokedToken;
    }

    private void revokeTokenFromGitHub(String accessToken) {
        webClientBuilder.build()
                .delete()
                .uri("https://api.github.com/applications/{client_id}/token", oauthConfig.getClientId())
                .header("Accept", "application/vnd.github+json")
                .header("Authorization", "Bearer " + accessToken)
                .retrieve()
                .bodyToMono(Void.class)
                .onErrorResume(error -> {
                    return Mono.empty();
                })
                .block();

    }
}