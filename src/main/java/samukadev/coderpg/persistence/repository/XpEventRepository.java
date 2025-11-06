package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.persistence.model.XpEventEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface XpEventRepository extends JpaRepository<XpEventEntity, UUID> {

    List<XpEventEntity> findByUserId(UUID userId);

    List<XpEventEntity> findByUserIdAndSkillType(UUID userId, SkillType skillType);

    List<XpEventEntity> findByUserIdAndXpSource(UUID userId, XpSource xpSource);

    List<XpEventEntity> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<XpEventEntity> findByGithubEventId(String githubEventId);

    boolean existsByGithubEventId(String githubEventId);

}
