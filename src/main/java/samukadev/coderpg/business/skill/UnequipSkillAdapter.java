package samukadev.coderpg.business.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.skill.UnequipSkillPort;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UnequipSkillAdapter implements UnequipSkillPort {

    private final SkillHistoryRepositoryPort repository;

    @Override
    public SkillHistory execute(final Context context) {

        UUID userId = context.getProperty("userId",  UUID.class);
        SkillType skillType = context.getProperty("skillType",  SkillType.class);
        String skillName = context.getProperty("skillName",  String.class);

        SkillHistory skill = repository
                .findByUserIdAndSkillTypeAndSkillName(userId, skillType, skillName)
                .orElseThrow(() -> new BusinessException("Skill not found"));

        if (!skill.getIsEquipped()) {
            throw new BusinessException("Skill is not equipped");
        }

        skill.setIsEquipped(false);
        skill.setUnequippedAt(LocalDateTime.now());

        return repository.save(skill);

    }

}
