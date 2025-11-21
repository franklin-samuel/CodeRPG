package samukadev.coderpg.business.utils;

import lombok.experimental.UtilityClass;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.persistence.repository.SkillHistoryRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

@UtilityClass
public class UpdateSkillProgress {

    private static final int SKILL_XP_PER_LEVEL = 1000;

    public static void execute(
            XpEvent xpEvent,
            Context context,
            SkillHistoryRepositoryPort skillHistoryRepositoryPort
    ) {
        Optional<SkillHistory> skillOpt = skillHistoryRepositoryPort.
                findByUserIdAndSkillTypeAndSkillName(
                        xpEvent.getUserId(),
                        xpEvent.getSkillType(),
                        xpEvent.getSkillName()
                );

        SkillHistory skill;
        boolean isNewSkill = false;

        if (skillOpt.isPresent()) {
            skill = skillOpt.get();
        } else {
            isNewSkill = true;
            skill = SkillHistory.builder()
                    .userId(xpEvent.getUserId())
                    .skillType(xpEvent.getSkillType())
                    .skillName(xpEvent.getSkillName())
                    .level(1)
                    .xp(0)
                    .isEquipped(false)
                    .active(true)
                    .build();
        }

        int previousSkillLevel = skill.getLevel();
        skill.setXp(skill.getXp() + xpEvent.getXpAmount());
        int newSkillLevel = calculateSkillLevel(skill.getXp());
        skill.setLevel(newSkillLevel);

        skillHistoryRepositoryPort.save(skill);

        if (newSkillLevel > previousSkillLevel) {
            context.putProperty("skillLeveledUp", true);
            context.putProperty("skillPreviousLevel", previousSkillLevel);
            context.putProperty("skillNewLevel", newSkillLevel);
        }

        if (isNewSkill) {
            context.putProperty("newSkillDiscovered", true);
        }
    }

    private static int calculateSkillLevel(int xp) {
        return (xp / SKILL_XP_PER_LEVEL) + 1;
    }

}
