package samukadev.coderpg.business.mission;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import samukadev.coderpg.business.utils.CalculateUserLevel;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.mission.CompleteMissionPort;
import samukadev.coderpg.core.persistence.UserMissionRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompleteMissionAdapter implements CompleteMissionPort {

    private final UserMissionRepositoryPort userMissionRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public UserMission execute(final Context context) {

        UUID missionId = context.getProperty("missionId", UUID.class);
        UUID userId = context.getProperty("userId", UUID.class);

        UserMission mission = userMissionRepository.get(missionId)
                .orElseThrow(() -> new BusinessException("Mission not found"));

        if (!mission.getUserId().equals(userId)) {
            throw new BusinessException("Mission does not belong to user");
        }

        if (mission.getCompleted()) {
            throw new BusinessException("Mission is already completed");
        }

        if (mission.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("Mission is expired");
        }

        if (mission.getProgress() < mission.getTarget()) {
            throw new BusinessException("Mission progress not completed");
        }

        mission.setCompleted(true);
        mission.setCompletedAt(LocalDateTime.now());

        User user = userRepository.get(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setXp(user.getXp() + mission.getRewardXp());
        user.setTotalXp(user.getTotalXp() + mission.getRewardXp());

        int newLevel = CalculateUserLevel.execute(user.getTotalXp());
        if (newLevel > user.getLevel()) {
            user.setLevel(newLevel);
        }

        userRepository.save(user);

        return userMissionRepository.save(mission);

    }
}
