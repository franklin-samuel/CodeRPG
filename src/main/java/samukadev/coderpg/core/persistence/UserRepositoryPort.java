package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.User;

import java.util.Optional;

public interface UserRepositoryPort extends BaseRepositoryPort<User> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByGithubId(Number githubId);

    Optional<User> findByGitHubId(Number githubId);

}
