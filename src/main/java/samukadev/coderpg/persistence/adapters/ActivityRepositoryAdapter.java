package samukadev.coderpg.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.persistence.ActivityRepositoryPort;
import samukadev.coderpg.domain.Activity;
import samukadev.coderpg.domain.enums.ActivityType;
import samukadev.coderpg.persistence.mappers.ActivityMapper;
import samukadev.coderpg.persistence.model.ActivityEntity;
import samukadev.coderpg.persistence.model.UserEntity;
import samukadev.coderpg.persistence.repository.ActivityRepository;
import samukadev.coderpg.persistence.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;

@Repository
@RequiredArgsConstructor
@Transactional
public class ActivityRepositoryAdapter implements ActivityRepositoryPort {

    private final ActivityRepository repository;
    private final ActivityMapper mapper;
    private final UserRepository userRepository;

    @Override
    public Optional<Activity> get(UUID id) {
        return repository.findById(id)
                .map(mapper::map);
    }

    @Override
    public Activity save(Activity model) {
        ActivityEntity entity = mapper.map(model);

        if (model.getUserId() != null) {
            UserEntity user = userRepository.findById(model.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setUser(user);
        }

        ActivityEntity entitySaved = repository.save(entity);
        return mapper.map(entitySaved);
    }

    @Override
    public List<Activity> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findByUserIdAndIsPublic(UUID userId, boolean isPublic) {
        return repository.findByUserIdAndIsPublic(userId, isPublic).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findByUserIdAndType(UUID userId, ActivityType type) {
        return repository.findByUserIdAndType(userId, type).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findPublicActivitiesByUsersIds(List<UUID> usersId) {
        return repository.findPublicActivitiesByUsersIds(usersId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after) {
        return repository.findByUserIdAndCreatedAtAfter(userId, after).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<Activity> findRecentPublicActivities(int limit) {
        return repository.findRecentPublicActivities(limit).stream()
                .map(mapper::map)
                .toList();
    }
}