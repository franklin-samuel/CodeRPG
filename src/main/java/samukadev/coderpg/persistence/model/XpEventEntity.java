package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "xp_events",
        indexes = {
                @Index(name = "idx_xp_events_user_id", columnList = "user_id"),
                @Index(name = "idx_xp_events_source", columnList = "xp_source"),
                @Index(name = "idx_xp_events_github_event", columnList = "github_event_id"),
                @Index(name = "idx_xp_events_skill_type", columnList = "skill_type"),
                @Index(name = "idx_xp_events_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class XpEventEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_xp_event_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "xp_source", nullable = false)
    private XpSource xpSource;

    @Column(name = "source_detail")
    private String sourceDetail;

    @Column(name = "xp_amount", nullable = false)
    private Integer xpAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "skill_type")
    private SkillType skillType;

    @Column(name = "skill_name")
    private String skillName;

    @Column(name = "github_event_id", unique = true)
    private String githubEventId;

    @Column(name = "github_repo")
    private String githubRepo;

    @Column(name = "github_url", columnDefinition = "TEXT")
    private String githubUrl;

    @Column(name = "multiplier", precision = 4, scale = 2)
    private BigDecimal multiplier;

    @ElementCollection
    @CollectionTable(
            name = "xp_event_multiplier_reasons",
            joinColumns = @JoinColumn(name = "xp_event_id"),
            foreignKey = @ForeignKey(name = "fk_multiplier_reasons_xp_event")
    )
    @Column(name = "reason")
    private List<String> multiplierReasons;
}