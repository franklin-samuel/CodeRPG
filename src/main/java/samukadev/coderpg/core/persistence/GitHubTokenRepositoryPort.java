package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.WriteRepositoryPort;
import samukadev.coderpg.domain.GitHubToken;

import java.util.Optional;
import java.util.UUID;

public interface GitHubTokenRepositoryPort extends WriteRepositoryPort<GitHubToken> {
    Optional<GitHubToken> findFirstByUserIdOrderByCreatedAtDesc(final UUID userId);
    Optional<GitHubToken> findByUserIdAndActiveTrue(UUID userId);
    void deactiveAllByUserId(final UUID userId);
}
