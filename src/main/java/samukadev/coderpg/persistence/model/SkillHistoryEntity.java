package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.SkillType;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "skill_history",
        indexes = {
                @Index(name = "idx_skill_history_user_id", columnList = "user_id"),
                @Index(name = "idx_skill_history_equipped", columnList = "user_id, is_equipped"),
                @Index(name = "idx_skill_history_skill_type", columnList = "user_id, skill_type")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_skill",
                        columnNames = {"user_id", "skill_type", "skill_name"}
                )
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class SkillHistoryEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_skill_history_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type", nullable = false, length = 50)
    private SkillType skillType;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "level", nullable = false)
    private Integer level;

    @Column(name = "xp", nullable = false)
    private Integer xp;

    @Column(name = "is_equipped", nullable = false)
    private Boolean isEquipped;

    @Column(name = "first_equipped_at")
    private LocalDateTime firstEquippedAt;

    @Column(name = "last_equipped_at")
    private LocalDateTime lastEquippedAt;

    @Column(name = "unequipped_at")
    private LocalDateTime unequippedAt;
}