package samukadev.coderpg.security.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.persistence.GitHubTokenRepositoryPort;
import samukadev.coderpg.core.security.GetActiveGitHubTokenPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GetActiveGitHubTokenAdapter implements GetActiveGitHubTokenPort {

    private final GitHubTokenRepositoryPort repository;

    @Override
    public GitHubToken execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);

        if (userId == null) {
            throw new BusinessException("User not found");
        }

        GitHubToken token = repository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseThrow(() -> new BusinessException("No github token found for user"));

        if (!token.getActive()) {
            throw new BusinessException("Token is inactive. Please login again");
        }

        if (token.getExpiresAt() != null && token.getExpiresAt().isBefore(LocalDateTime.now())) {
            context.putProperty("tokenExpired", true);
            throw new BusinessException("GitHub token expired. Refresh required.");
        }

        context.putProperty("tokenValid", true);
        context.putProperty("accessToken", token.getGithubAccessToken());

        return token;

    }

}
