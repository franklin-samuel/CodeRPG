package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.persistence.model.UserBuildEntity;

import java.util.Optional;
import java.util.UUID;

public interface UserBuildRepository extends JpaRepository<UserBuildEntity, UUID> {

    Optional<UserBuildEntity> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

}
