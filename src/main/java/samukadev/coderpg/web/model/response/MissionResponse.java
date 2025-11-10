package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samukadev.coderpg.domain.enums.Difficulty;
import samukadev.coderpg.domain.enums.MissionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MissionResponse {

    private UUID id;
    private String missionId;
    private MissionType missionType;

    private String title;
    private String description;
    private String icon;

    private Integer rewardXp;
    private Difficulty difficulty;

    private Integer progress;
    private Integer target;
    private Integer progressPercentage;

    private Boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;

    private Boolean isExpired;
    private Long timeRemaining;

    private LocalDateTime createdAt;

}

