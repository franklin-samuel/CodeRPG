package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.WriteRepositoryPort;
import samukadev.coderpg.domain.GitHubToken;

import java.util.Optional;
import java.util.UUID;

public interface GitHubTokenRepositoryPort extends WriteRepositoryPort<GitHubToken> {
    Optional<GitHubToken> findByToken(final String token);
    Optional<GitHubToken> findLatestByUserId(final UUID userId);
}
