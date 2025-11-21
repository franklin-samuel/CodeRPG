package samukadev.coderpg.business.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.skill.SaveSkillProgressPort;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SaveSkillProgressAdapter implements SaveSkillProgressPort {

    private final SkillHistoryRepositoryPort skillHistoryRepository;

    @Override
    public SkillHistory execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);
        SkillType skillType = context.getProperty("skillType", SkillType.class);
        String skillName = context.getProperty("skillName", String.class);
        Integer xpToAdd = context.getProperty("xpToAdd", Integer.class);

        if (xpToAdd == null || xpToAdd <= 0) {
            throw new BusinessException("XP to add must be greater than 0");
        }

        Optional<SkillHistory> existingSkill = skillHistoryRepository
                .findByUserIdAndSkillTypeAndSkillName(userId, skillType, skillName);

        SkillHistory skill;
        boolean isNewSkill = false;

        if (existingSkill.isPresent()) {
            skill = existingSkill.get();
        } else {
            isNewSkill = true;
            skill = SkillHistory.builder()
                    .userId(userId)
                    .skillType(skillType)
                    .skillName(skillName)
                    .level(1)
                    .xp(0)
                    .isEquipped(false)
                    .firstEquippedAt(null)
                    .active(true)
                    .build();
        }

        int currentXp = skill.getXp() +  xpToAdd;
        int currentLevel = skill.getLevel();
        int newLevel = currentXp / 1000 + 1;

        skill.setXp(currentXp);
        skill.setLevel(newLevel);

        SkillHistory savedSkill = skillHistoryRepository.save(skill);

        if (newLevel > currentLevel) {
            context.putProperty("leveledUp", true);
            context.putProperty("previousLevel", currentLevel);
            context.putProperty("newLevel", newLevel);
        }

        if (isNewSkill) {
            context.putProperty("newSkillCreated", true);
        }

        return savedSkill;

    }

}
