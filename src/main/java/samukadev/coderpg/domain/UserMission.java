package samukadev.coderpg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.Difficulty;
import samukadev.coderpg.domain.enums.MissionType;

import java.time.LocalDateTime;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserMission extends AbstractDomain {
    private UUID userId;
    private String missionId;
    private MissionType missionType;
    private String title;
    private String description;
    private Integer rewardXp;
    private Difficulty difficulty;
    private String icon;
    private Integer progress;
    private Integer target;
    private boolean completed;
    private LocalDateTime completedAt;
    private LocalDateTime expiresAt;
}
