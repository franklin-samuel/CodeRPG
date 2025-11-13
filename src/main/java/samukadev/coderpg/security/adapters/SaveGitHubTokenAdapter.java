package samukadev.coderpg.security.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.persistence.GitHubTokenRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.core.security.SaveGitHubTokenPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SaveGitHubTokenAdapter implements SaveGitHubTokenPort {

    private final GitHubTokenRepositoryPort tokenRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public GitHubToken execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);
        String accessToken = context.getProperty("accessToken", String.class);
        String refreshToken = context.getProperty("refreshToken", String.class);
        LocalDateTime expiresAt = context.getProperty("expiresAt", LocalDateTime.class);

        if (userId == null) {
            throw new BusinessException("User ID is required!");
        }

        if (accessToken == null || accessToken.isBlank()) {
            throw new BusinessException("Access token is required");
        }

        User user = userRepository.get(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Optional<GitHubToken> existingToken = tokenRepository.findFirstByUserIdOrderByCreatedAtDesc(userId);
        if (existingToken.isPresent()) {
            GitHubToken oldToken = existingToken.get();
            oldToken.setActive(false);
            tokenRepository.save(oldToken);
        }

        tokenRepository.deactiveAllByUserId(userId);
        GitHubToken newToken = GitHubToken.builder()
                .userId(userId)
                .user(user)
                .githubAccessToken(accessToken)
                .githubRefreshToken(refreshToken)
                .expiresAt(expiresAt)
                .active(true)
                .build();

        GitHubToken savedToken = tokenRepository.save(newToken);

        context.putProperty("tokenId", savedToken.getId());

        return savedToken;
    }

}
