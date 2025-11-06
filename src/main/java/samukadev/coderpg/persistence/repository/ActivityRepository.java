package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import samukadev.coderpg.domain.enums.ActivityType;
import samukadev.coderpg.persistence.model.ActivityEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ActivityRepository extends JpaRepository<ActivityEntity, UUID> {

    List<ActivityEntity> findByUserId(UUID userId);

    List<ActivityEntity> findByUserIdAndIsPublic(UUID userId, Boolean isPublic);

    List<ActivityEntity> findByUserIdAndType(UUID userId, ActivityType type);

    @Query("SELECT a FROM ActivityEntity a WHERE a.user.id IN :userIds AND a.isPublic = true AND a.active = true ORDER BY a.createdAt DESC")
    List<ActivityEntity> findPublicActivitiesByUsersIds(@Param("userIds") List<UUID> userIds);

    List<ActivityEntity> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after);

    @Query("SELECT a FROM ActivityEntity a WHERE a.isPublic = true AND a.active = true ORDER BY a.createdAt DESC LIMIT :limit")
    List<ActivityEntity> findRecentPublicActivities(@Param("limit") Integer limit);

}
