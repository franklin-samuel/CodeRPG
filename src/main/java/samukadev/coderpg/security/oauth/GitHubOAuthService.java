package samukadev.coderpg.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.core.security.GetActiveGitHubTokenPort;
import samukadev.coderpg.core.security.RefreshGitHubTokenPort;
import samukadev.coderpg.core.security.RevokeGitHubTokenPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubOAuthService {

    private final GetActiveGitHubTokenPort getActiveTokenPort;
    private final RefreshGitHubTokenPort refreshTokenPort;
    private final RevokeGitHubTokenPort revokeTokenPort;

    public String getAccessToken(UUID userId) {
        Context context =  new Context();
        context.putProperty("userId", userId);

        try {
            GitHubToken token = getActiveTokenPort.execute(context);
            return token.getGithubAccessToken();
        } catch (BusinessException e) {
            if (context.getProperty("tokenExpired", Boolean.class) != null) {
                return refreshAccessToken(userId);
            }
            throw e;
        }
    }

    public String refreshAccessToken(UUID userId) {
        Context context =  new Context();
        context.putProperty("userId", userId);

        GitHubToken newToken = refreshTokenPort.execute(context);

        return newToken.getGithubAccessToken();
    }

    public void revokeUserToken(UUID userId) {
        Context context =  new Context();
        context.putProperty("userId", userId);

        revokeTokenPort.execute(context);
    }

    public boolean hasValideToken(UUID userId) {
        Context context =  new Context();
        context.putProperty("userId", userId);

        try {
            GitHubToken token = getActiveTokenPort.execute(context);

            if (token.getExpiresAt() != null) {
                return token.getExpiresAt().isAfter(LocalDateTime.now());
            }

            return true;
        } catch (BusinessException e) {
            return false;
        }
    }

}