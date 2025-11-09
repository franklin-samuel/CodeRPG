package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.persistence.model.GitHubTokenEntity;
import samukadev.coderpg.persistence.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface GitHubTokenRepository extends JpaRepository<GitHubTokenEntity, UUID> {
    Optional<GitHubTokenEntity> findByToken(final String token);
    Optional<GitHubTokenEntity> findLatestByUserId(final UUID userId);

    UUID user(UserEntity user);
}
