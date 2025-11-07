package samukadev.coderpg.business.utils;

import lombok.experimental.UtilityClass;
import samukadev.coderpg.domain.enums.DailyMissionType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@UtilityClass
public class SelectRandomMissions {
    public static List<DailyMissionType> execute(int count, Random random) {
        List<DailyMissionType> selected = new ArrayList<>();
        List<DailyMissionType> available = new ArrayList<>(Arrays.asList(DailyMissionType.values()));

        for (int i = 0; i < Math.min(count, available.size()); i++) {
            int index = random.nextInt(available.size());
            selected.add(available.remove(index));
        }

        return selected;
    }
}
