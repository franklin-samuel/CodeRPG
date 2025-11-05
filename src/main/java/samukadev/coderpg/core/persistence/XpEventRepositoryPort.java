package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface XpEventRepositoryPort extends BaseRepositoryPort<XpEvent> {

    List<XpEvent> findByUserId(UUID userId);

    List<XpEvent> findByUserIdAndSkillType(UUID userId, SkillType skillType);

    List<XpEvent> findByUserIdAndXpSource(UUID userId, XpSource xpSource);

    List<XpEvent> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    );

    Optional<XpEvent> findByGithubEventId(String githubEventId);

    boolean existsByGithubEventId(String githubEventId);



}
