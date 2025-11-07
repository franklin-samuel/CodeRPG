package samukadev.coderpg.business.skill;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.skill.GetSkillHistoryPort;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GetSkillHistoryAdapter implements GetSkillHistoryPort {

    private final SkillHistoryRepositoryPort repository;

    @Override
    public SkillHistory execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);

        List<SkillHistory> skills = repository.findByUserId(userId);

        if (skills.isEmpty()) {
            throw new BusinessException("No skills found for user");
        }

        context.putProperty("skillsList", skills);
        context.putProperty("totalSkills", skills.size());
        context.putProperty("equippedSkills", skills.stream().filter(SkillHistory::getIsEquipped).count());

        return skills.getFirst();

    }

}
