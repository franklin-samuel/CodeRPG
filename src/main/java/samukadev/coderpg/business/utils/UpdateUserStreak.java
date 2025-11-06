package samukadev.coderpg.business.utils;

import lombok.experimental.UtilityClass;
import samukadev.coderpg.domain.User;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@UtilityClass
public class UpdateUserStreak {
    public static void execute(User user) {

        LocalDate today = LocalDate.now();
        LocalDate lastActivity = user.getLastActivityDate();

        if (lastActivity == null) {
            user.setCurrentStreak(1);
            user.setLongestStreak(1);
        } else {
            long daysBetween = ChronoUnit.DAYS.between(lastActivity, today);

            if (daysBetween == 0) {
                return;
            } else if (daysBetween == 1) {
                user.setCurrentStreak(user.getCurrentStreak() + 1);
                if (user.getCurrentStreak() > user.getLongestStreak()) {
                    user.setLongestStreak(user.getCurrentStreak());
                }
            } else {
                user.setCurrentStreak(1);
            }
        }
        user.setLastActivityDate(today);
    }
}
