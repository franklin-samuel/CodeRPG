package samukadev.coderpg.business.utils;

import lombok.experimental.UtilityClass;

@UtilityClass
public class CalculateUserLevel {
    public static int execute(Long totalXp) {
        return (int) Math.floor(Math.sqrt(totalXp / 100.0)) + 1;
    }
}
