package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import samukadev.coderpg.domain.enums.MissionType;
import samukadev.coderpg.persistence.model.UserMissionEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserMissionRepository extends JpaRepository<UserMissionEntity, UUID> {

    List<UserMissionEntity> findByUserId(UUID userId);

    List<UserMissionEntity> findByUserIdAndCompleted(UUID userId, Boolean completed);

    List<UserMissionEntity> findByUserIdAndMissionType(UUID userId, MissionType missionType);

    Optional<UserMissionEntity> findByUserIdAndMissionId(UUID userId, String missionId);

    List<UserMissionEntity> findByUserIdAndExpiresAtBefore(UUID userId, LocalDateTime expiresAt);

    @Query("SELECT um FROM UserMissionEntity um WHERE um.user.id = :userId AND um.completed = false AND um.expiresAt > CURRENT_TIMESTAMP AND um.active = true")
    List<UserMissionEntity> findActiveByUserId(@Param("userId") UUID userId);

}
