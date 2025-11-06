package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    @Query("SELECT sh FROM SkillHistoryEntity sh WHERE sh.user.id = :userId AND sh.active = true")
    List<SkillHistoryEntity> findAllSkillsByUserId(@Param("userId") UUID userId);

    @Query("SELECT COUNT(DISTINCT sh.skillName) FROM SkillHistoryEntity sh WHERE sh.user.id = :userId AND sh.active = true")
    long countDistinctSkillsByUserId(@Param("userId") UUID userId);

    @Modifying
    @Query("UPDATE SkillHistoryEntity sh SET sh.isEquipped = false, sh.unequippedAt = CURRENT_TIMESTAMP WHERE sh.user.id = :userId AND sh.skillType = :skillType")
    void unequipAllByUserIdAndSkillType(@Param("userId") UUID userId, @Param("skillType") SkillType skillType);

}
