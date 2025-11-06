package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.persistence.model.SkillHistoryEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillHistoryRepository extends JpaRepository<SkillHistoryEntity, UUID> {

    List<SkillHistoryEntity> findByUserId(UUID userId);

    List<SkillHistoryEntity> findByUserIdAndIsEquipped(UUID userId, Boolean isEquipped);

    Optional<SkillHistoryEntity> findByUserIdAndSkillTypeAndSkillName(
            UUID userId,
            SkillType skillType,
            String skillName
    );

    List<SkillHistoryEntity> findByUserIdAndSkillType(UUID userId, SkillType skillType);

    boolean existsByUserIdAndSkillTypeAndSkillName(
            UUID userId,
            SkillType skillType,
            String skillName
    );

    List<SkillHistoryEntity> findAllSkillsByUserId(UUID userId);

    long countDistinctSkillsByUserId(UUID userId);

    void unequipAllByUserIdAndSkillType(UUID userId, SkillType skillType);

}
