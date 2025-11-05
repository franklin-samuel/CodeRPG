package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.UserBuild;

import java.util.Optional;
import java.util.UUID;

public interface UserBuildRepositoryPort extends BaseRepositoryPort<UserBuild> {

    Optional<UserBuild> findByUserId(UUID userId);

    void deleteByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

}
