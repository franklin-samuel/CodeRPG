package samukadev.coderpg.business.xp;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CalculateXpMultiplierAdapter implements CalculateXpMultiplierPort {

    private final UserRepositoryPort userRepository;
    private  final SkillHistoryRepositoryPort skillHistoryRepository;

    @Override
    public XpEvent execute(final Context context) {

        XpEvent xpEvent = context.getData(XpEvent.class);

        if (xpEvent == null || xpEvent.getUserId() == null) {
            throw new BusinessException("XP Event with user ID is required.");
        }

        User user = userRepository.get(xpEvent.getUserId())
                .orElseThrow(() -> new BusinessException("User not found."));

        BigDecimal multiplier = BigDecimal.ONE;
        List<String> reasons = new ArrayList<>();

        // +10% per 7 days streak (max +50%)
        if (user.getCurrentStreak() != null && user.getCurrentStreak() >= 7) {
            int streakBonus = Math.min((user.getCurrentStreak() / 7) * 10, 50);
            BigDecimal streakMultiplier = BigDecimal.valueOf(streakBonus).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            multiplier = multiplier.add(streakMultiplier);
            reasons.add(String.format("Streak bonus: +%d%% (%d days)", streakBonus, user.getCurrentStreak()));
        }

        // +25% if skill matches equipped skill
        if (xpEvent.getSkillType() != null && xpEvent.getSkillName() != null) {
            List<SkillHistory> equippedSkills = skillHistoryRepository
                    .findByUserIdAndIsEquipped(xpEvent.getUserId(), true);

            boolean skillMatches = equippedSkills.stream()
                    .anyMatch(skill ->
                            skill.getSkillType().equals(xpEvent.getSkillType()) &&
                                    skill.getSkillName().equalsIgnoreCase(xpEvent.getSkillName())
                    );

            if (skillMatches) {
                multiplier = multiplier.add(BigDecimal.valueOf(0.25));
                reasons.add("Equipped skill bonus: +25%");
            }
        }

        // +15% on weekends
        DayOfWeek dayOfWeek = LocalDateTime.now().getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            multiplier = multiplier.add(BigDecimal.valueOf(0.15));
            reasons.add("Weekend bonus: +15%");
        }

        // +5% per 10 levels
        if (user.getLevel() != null && user.getLevel() >= 10) {
            int levelBonus = Math.min((user.getLevel() / 10) * 5, 25);
            BigDecimal levelMultiplier = BigDecimal.valueOf(levelBonus).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            multiplier = multiplier.add(levelMultiplier);
            reasons.add(String.format("Level bonus: +%d%% (Level %d)", levelBonus, user.getLevel()));
        }

        int originalXp = xpEvent.getXpAmount();
        int multipliedXp = multiplier.multiply(BigDecimal.valueOf(originalXp))
                .setScale(0, RoundingMode.HALF_UP)
                .intValue();

        xpEvent.setXpAmount(multipliedXp);
        xpEvent.setMultiplier(multiplier);
        xpEvent.setMultiplierReasons(reasons);

        return xpEvent;

    }

}
