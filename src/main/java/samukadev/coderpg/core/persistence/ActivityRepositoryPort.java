package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.Activity;
import samukadev.coderpg.domain.enums.ActivityType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityRepositoryPort extends BaseRepositoryPort<Activity> {

    List<Activity> findByUserId(UUID userId);

    List<Activity> findByUserIdAndIsPublic(UUID userId, boolean isPublic);

    List<Activity> findByUserIdAndType(UUID userId, ActivityType type);

    List<Activity> findPublicActivitiesByUsersIds(List<UUID> usersId);

    List<Activity> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after);

    List<Activity> findRecentPublicActivities(int limit);

}
