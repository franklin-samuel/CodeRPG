package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import samukadev.coderpg.persistence.model.GitHubTokenEntity;
import samukadev.coderpg.persistence.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface GitHubTokenRepository extends JpaRepository<GitHubTokenEntity, UUID> {
    Optional<GitHubTokenEntity> findByToken(final String token);
    Optional<GitHubTokenEntity> findLatestByUserId(final UUID userId);
    Optional<GitHubTokenEntity> findByUserIdAndActiveTrue(final UUID userId);

    @Modifying
    @Query("UPDATE GitHubTokenEntity t SET t.active = false WHERE t.user.id = :userId")
    void deactiveAllByUserId(@Param("userId") UUID userId);
}
