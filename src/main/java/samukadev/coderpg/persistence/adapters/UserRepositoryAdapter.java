package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.core.persistence.commons.ReadRepositoryPort;
import samukadev.coderpg.core.persistence.commons.WriteRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.persistence.mappers.UserMapper;
import samukadev.coderpg.persistence.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserRepositoryAdapter implements UserRepositoryPort, ReadRepositoryPort<User>, WriteRepositoryPort<User> {

    private final UserRepository repository;

    private final UserMapper mapper;

    @Override
    public Optional<User> get(final UUID id) {
        return repository.findById(id).map(mapper::map);
    }

    @Override
    public User save(final User model) {
        return of(repository.save(mapper.map(model)))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to save user"));
    }

    @Override
    public List<User> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map).toList();
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return repository.findByEmail(email)
                .map(mapper::map);
    }

    @Override
    public boolean existsByEmail(final String email) {
        return repository.existsByEmail(email);
    }

    @Override
    public boolean existsByGithubId(final Number githubId) {
        return repository.existsByGithubId(githubId);
    }

    @Override
    public Optional<User> findByGitHubId(final Number githubId) {
        return repository.findByGithubId(githubId)
                .map(mapper::map);
    }

}
