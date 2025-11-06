package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import samukadev.coderpg.core.persistence.UserFollowRepositoryPort;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.persistence.mappers.UserFollowMapper;
import samukadev.coderpg.persistence.repository.UserFollowRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserFollowRepositoryAdapter implements UserFollowRepositoryPort {

    private final UserFollowRepository repository;

    private final UserFollowMapper mapper;

    @Override
    public Optional<UserFollow> get(UUID id) {
        return repository.findById(id)
                .map(mapper::map);
    }

    @Override
    public UserFollow save(final UserFollow model) {
        return of(repository.save(mapper.map(model)))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to save user follow"));
    }

    @Override
    public List<UserFollow> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<UserFollow> findByFollowerId(UUID followerId) {
        return repository.findByFollowerId(followerId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<UserFollow> findByFollowingId(UUID followingId) {
        return repository.findByFollowingId(followingId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public Optional<UserFollow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId) {
        return repository.findByFollowerIdAndFollowingId(followerId, followingId)
                .map(mapper::map);
    }

    @Override
    public boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId) {
        return repository.existsByFollowerIdAndFollowingId(followerId, followingId);
    }

    @Override
    public long countByFollowerId(UUID followerId) {
        return repository.countByFollowerId(followerId);
    }

    @Override
    public long countByFollowingId(UUID followingId) {
        return repository.countByFollowingId(followingId);
    }

    @Override
    @Transactional
    public void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId) {
        repository.deleteByFollowerIdAndFollowingId(followerId, followingId);
    }

}
