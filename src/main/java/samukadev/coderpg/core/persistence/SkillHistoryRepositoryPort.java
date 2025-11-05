package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.enums.SkillType;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillHistoryRepositoryPort extends BaseRepositoryPort<SkillHistory> {

    List<SkillHistory> findByUserId(UUID userId);

    List<SkillHistory> findByUserIdAndIsEquipped(UUID userId, boolean isEquipped);

    Optional<SkillHistory> findByUserIdAndSkillTypeAndSkillName(
            UUID userId,
            SkillType skillType,
            String skillName
    );

    List<SkillHistory> findByUserIdAndSkillType(UUID userId, SkillType skillType);

    boolean existsByUserIdAndSkillTypeAndSkillName(
            UUID userId,
            SkillType skillType,
            String skillName
    );

    List<SkillHistory> findAllSkillsByUserId(UUID userId);

    long countDistinctSkillsByUserId(UUID userId);

    void unequipAllByUserIdAndSkillType(UUID userId, SkillType skillType);

}
