package samukadev.coderpg.business.mission;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import samukadev.coderpg.business.utils.SelectRandomMissions;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.mission.GenerateDailyMissionsPort;
import samukadev.coderpg.core.persistence.UserMissionRepositoryPort;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.domain.enums.DailyMissionType;
import samukadev.coderpg.domain.enums.MissionType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GenerateDailyMissionsAdapter implements GenerateDailyMissionsPort {

    private final UserMissionRepositoryPort userMissionRepository;
    private final Random random = new Random();

    @Override
    public UserMission execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);

        List<UserMission> activeMissions = userMissionRepository.findByUserIdAndMissionType(userId, MissionType.DAILY)
                .stream()
                .filter(m -> !m.getCompleted() && m.getExpiresAt().isAfter(LocalDateTime.now()))
                .toList();

        if (!activeMissions.isEmpty()) {
            return activeMissions.getFirst();
        }

        List<DailyMissionType> selectedMissions = SelectRandomMissions.execute(3, random);
        List<UserMission> newMissions = new ArrayList<>();

        LocalDateTime expiresAt = LocalDateTime.now().plusDays(1).withHour(23).withMinute(59).withSecond(59);

        for (DailyMissionType missionType : selectedMissions) {
            UserMission mission = UserMission.builder()
                    .userId(userId)
                    .missionId(missionType.name())
                    .missionType(MissionType.DAILY)
                    .title(missionType.getTitle())
                    .description(missionType.getDescription())
                    .rewardXp(missionType.getRewardXp())
                    .difficulty(missionType.getDifficulty())
                    .icon(missionType.getIcon())
                    .progress(0)
                    .target(missionType.getTarget())
                    .completed(false)
                    .expiresAt(expiresAt)
                    .active(true)
                    .build();

            newMissions.add(userMissionRepository.save(mission));
        }

        return newMissions.isEmpty() ? null : newMissions.getFirst();
    }

}
