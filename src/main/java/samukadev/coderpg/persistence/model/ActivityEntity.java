package samukadev.coderpg.persistence.model;

import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Type;
import samukadev.coderpg.domain.enums.ActivityType;

import java.util.Map;
import java.util.UUID;

@Entity
@Table(
        name = "activities",
        indexes = {
                @Index(name = "idx_activities_user_id", columnList = "user_id"),
                @Index(name = "idx_activities_type", columnList = "type"),
                @Index(name = "idx_activities_is_public", columnList = "is_public"),
                @Index(name = "idx_activities_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class ActivityEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_activity_user"))
    private UserEntity user;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private ActivityType type;

    @Type(JsonType.class)
    @Column(name = "data", columnDefinition = "jsonb", nullable = false)
    private Map<String, Object> data;

    @Column(name = "is_public", nullable = false)
    private Boolean isPublic;

    @Column(name = "likes_count", nullable = false)
    private Integer likesCount;

    @Column(name = "comments_count", nullable = false)
    private Integer commentsCount;
}