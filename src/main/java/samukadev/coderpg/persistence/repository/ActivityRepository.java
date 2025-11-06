package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.domain.enums.ActivityType;
import samukadev.coderpg.persistence.model.ActivityEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {

    List<ActivityEntity> findByUserId(UUID userId);

    List<ActivityEntity> findByUserIdAndIsPublic(UUID userId, Boolean isPublic);

    List<ActivityEntity> findByUserIdAndType(UUID userId, ActivityType type);

    List<ActivityEntity> findPublicActivitiesByUsersIds(List<UUID> usersId);

    List<ActivityEntity> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after);

    List<ActivityEntity> findRecentPublicActivities(Integer limit);

}
