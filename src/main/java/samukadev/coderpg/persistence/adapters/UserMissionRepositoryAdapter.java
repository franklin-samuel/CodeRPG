package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import samukadev.coderpg.core.persistence.UserMissionRepositoryPort;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.domain.enums.MissionType;
import samukadev.coderpg.persistence.mappers.UserMissionMapper;
import samukadev.coderpg.persistence.repository.UserMissionRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserMissionRepositoryAdapter implements UserMissionRepositoryPort {

    private final UserMissionRepository repository;

    private final UserMissionMapper mapper;

    @Override
    public Optional<UserMission> get(UUID id) {
        return repository.findById(id)
                .map(mapper::map);
    }

    @Override
    public UserMission save(UserMission model) {
        return of(repository.save(mapper.map(model)))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to save user mission"));
    }

    @Override
    public List<UserMission> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<UserMission> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<UserMission> findByUserIdAndCompleted(UUID userId, boolean completed) {
        return repository.findByUserIdAndCompleted(userId, completed).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<UserMission> findByUserIdAndMissionType(UUID userId, MissionType missionType) {
        return repository.findByUserIdAndMissionType(userId, missionType).stream()
                .map(mapper::map).toList();
    }

    @Override
    public Optional<UserMission> findByUserIdAndMissionId(UUID userId, String missionId) {
        return repository.findByUserIdAndMissionId(userId, missionId)
                .map(mapper::map);
    }

    @Override
    public List<UserMission> findByUserIdAndExpiresAtBefore(UUID userId, LocalDateTime expiresAt) {
        return repository.findByUserIdAndExpiresAtBefore(userId, expiresAt).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<UserMission> findActiveByUserId(UUID userId) {
        return repository.findActiveByUserId(userId).stream()
                .map(mapper::map).toList();
    }

}
