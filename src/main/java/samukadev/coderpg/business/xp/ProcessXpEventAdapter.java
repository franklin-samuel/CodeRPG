package samukadev.coderpg.business.xp;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import samukadev.coderpg.business.utils.CalculateUserLevel;
import samukadev.coderpg.business.utils.UpdateSkillProgress;
import samukadev.coderpg.business.utils.UpdateUserStreak;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.core.persistence.XpEventRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.exceptions.BusinessException;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProcessXpEventAdapter implements ProcessXpEventPort {

    private final XpEventRepositoryPort xpEventRepositoryPort;
    private final UserRepositoryPort userRepositoryPort;
    private final SkillHistoryRepositoryPort skillHistoryRepositoryPort;

    @Override
    public XpEvent execute(final Context context) {

        XpEvent xpEvent = context.getData(XpEvent.class);

        if (xpEvent == null || xpEvent.getUserId() == null) {
            throw new BusinessException("XP event with user ID is required.");
        }

        if (xpEvent.getGithubEventId() != null &&
            xpEventRepositoryPort.existsByGithubEventId(xpEvent.getGithubEventId())) {
            throw new BusinessException("XP event with GITHUB ID already processed.");
        }

        User user = userRepositoryPort.get(xpEvent.getUserId())
                .orElseThrow(() -> new BusinessException("User not found"));

        int previousLevel = user.getLevel();
        user.setXp(user.getXp() + xpEvent.getXpAmount());
        user.setTotalXp(user.getTotalXp() + xpEvent.getXpAmount());

        int newLevel = CalculateUserLevel.execute(user.getTotalXp());
        user.setLevel(newLevel);

        UpdateUserStreak.execute(user);

        userRepositoryPort.save(user);

        if (newLevel > previousLevel) {
            context.putProperty("leveledUp", true);
            context.putProperty("previousLevel", previousLevel);
            context.putProperty("newLevel", newLevel);
        }

        if (xpEvent.getSkillType() != null && xpEvent.getSkillName() != null) {
            UpdateSkillProgress.execute(xpEvent, context, skillHistoryRepositoryPort);
        }

        return xpEventRepositoryPort.save(xpEvent);
    }

    private int calculateUserLevel(Long totalXp) {
        return (int) Math.floor(Math.sqrt(totalXp / 100.0)) + 1;
    }

}
