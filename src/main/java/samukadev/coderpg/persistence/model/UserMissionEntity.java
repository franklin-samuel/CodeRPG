package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.Difficulty;
import samukadev.coderpg.domain.enums.MissionType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_missions",
        indexes = {
                @Index(name = "idx_user_missions_type", columnList = "mission_type"),
                @Index(name = "idx_user_missions_completed", columnList = "completed"),
                @Index(name = "idx_user_missions_expires_at", columnList = "expires_at"),
                @Index(name = "idx_user_missions_user_active", columnList = "user_id, completed, expires_at")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserMissionEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_user_mission_user"))
    private UserEntity user;

    @Column(name = "mission_id", nullable = false)
    private String missionId;

    @Enumerated(EnumType.STRING)
    @Column(name = "mission_type", nullable = false)
    private MissionType missionType;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "reward_xp", nullable = false)
    private Integer rewardXp;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", nullable = false)
    private Difficulty difficulty;

    @Column(name = "icon")
    private String icon;

    @Column(name = "progress", nullable = false)
    private Integer progress;

    @Column(name = "target", nullable = false)
    private Integer target;

    @Column(name = "completed", nullable = false)
    private Boolean completed;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
}