package samukadev.coderpg.web.mappers;

import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.web.model.response.MissionResponse;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class MissionModelMapper {

    public MissionResponse toResponse(UserMission mission) {
        if (mission == null) return null;

        int progressPercentage = calculateProgressPercentage(
                mission.getProgress(),
                mission.getTarget()
        );

        boolean isExpired = mission.getExpiresAt() != null &&
                mission.getExpiresAt().isBefore(LocalDateTime.now());

        Long timeRemaining = calculateTimeRemaining(mission.getExpiresAt());

        return MissionResponse.builder()
                .id(mission.getId())
                .missionId(mission.getMissionId())
                .missionType(mission.getMissionType())
                .title(mission.getTitle())
                .description(mission.getDescription())
                .icon(mission.getIcon())
                .rewardXp(mission.getRewardXp())
                .difficulty(mission.getDifficulty())
                .progress(mission.getProgress())
                .target(mission.getTarget())
                .progressPercentage(progressPercentage)
                .completed(mission.getCompleted())
                .completedAt(mission.getCompletedAt())
                .expiresAt(mission.getExpiresAt())
                .isExpired(isExpired)
                .timeRemaining(timeRemaining)
                .createdAt(mission.getCreatedAt())
                .build();
    }

    private int calculateProgressPercentage(Integer progress, Integer target) {
        if (progress == null || target == null || target == 0) return 0;
        return (int) ((progress.doubleValue() / target.doubleValue()) * 100);
    }

    private Long calculateTimeRemaining(LocalDateTime expiresAt) {
        if (expiresAt == null) return null;
        LocalDateTime now = LocalDateTime.now();
        if (expiresAt.isBefore(now)) return 0L;
        return Duration.between(now, expiresAt).getSeconds();
    }
    
}
