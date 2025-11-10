package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.persistence.model.UserEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByGithubId(Number githubId);

    Optional<UserEntity> findByGithubId(Number githubId);

}
