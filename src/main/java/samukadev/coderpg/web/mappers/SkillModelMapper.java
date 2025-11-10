package samukadev.coderpg.web.mappers;

import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.web.model.response.SkillResponse;

@Component
public class SkillModelMapper {

    private static final int XP_PER_LEVEL = 1000;

    public SkillResponse toResponse(SkillHistory skill) {
        if (skill == null) return null;

        int xpToNextLevel = calculateXpToNextLevel(skill.getXp());

        return SkillResponse.builder()
                .id(skill.getId())
                .userId(skill.getUserId())
                .skillType(skill.getSkillType())
                .skillTypeName(skill.getSkillType() != null ? skill.getSkillType().getDisplayName() : null)
                .skillName(skill.getSkillName())
                .level(skill.getLevel())
                .xp(skill.getXp())
                .xpToNextLevel(xpToNextLevel)
                .isEquipped(skill.getIsEquipped())
                .firstEquippedAt(skill.getFirstEquippedAt())
                .lastEquippedAt(skill.getLastEquippedAt())
                .unequippedAt(skill.getUnequippedAt())
                .createdAt(skill.getCreatedAt())
                .build();
    }

    private int calculateXpToNextLevel(Integer currentXp) {
        if (currentXp == null) return XP_PER_LEVEL;
        int currentLevelXp = currentXp % XP_PER_LEVEL;
        return XP_PER_LEVEL - currentLevelXp;
    }

}

