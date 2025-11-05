package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.domain.enums.MissionType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMissionRepositoryPort extends BaseRepositoryPort<UserMission> {

    List<UserMission> findByUserId(UUID userId);

    List<UserMission> findByUserIdAndCompleted(UUID userId, boolean completed);

    List<UserMission> findByUserIdAndMissionType(UUID userId, MissionType missionType);

    Optional<UserMission> findByUserIdAndMissionId(UUID userId, String missionId);

    List<UserMission> findByUserIdAndExpiresAtBefore(UUID userId, LocalDateTime expiresAt);

    List<UserMission> findActiveByUserId(UUID userId);

}
