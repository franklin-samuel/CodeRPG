package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.enums.SyncStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_github_id", columnList = "github_id"),
                @Index(name = "idx_users_github_username", columnList = "github_username"),
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_level", columnList = "level"),
                @Index(name = "idx_users_total_xp", columnList = "total_xp")
        }
)
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class UserEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToMany(mappedBy = "follower", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFollowEntity> following = new ArrayList<>();

    @OneToMany(mappedBy = "following", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserFollowEntity> followers = new ArrayList<>();

    @Column(name = "github_id", unique = true, nullable = false)
    private String githubId;

    @Column(name = "github_username", unique = true, nullable = false)
    private String githubUsername;

    @Column(name = "name")
    private String name;

    @Column(name = "email")
    private String email;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "location")
    private String location;

    @Column(name = "website")
    private String website;

    @Enumerated(EnumType.STRING)
    @Column(name = "class_type", nullable = false)
    private ClassType classType;

    @Column(name = "level", nullable = false, columnDefinition = "INTEGER DEFAULT 1")
    private Integer level;

    @Column(name = "xp", nullable = false, columnDefinition = "INTEGER DEFAULT 0")
    private Integer xp;

    @Column(name = "total_xp", nullable = false, columnDefinition = "BIGINT DEFAULT 0")
    private Long totalXp;

    @Column(name = "github_public_repos", columnDefinition = "INTEGER DEFAULT 0")
    private Integer githubPublicRepos;

    @Column(name = "github_followers", columnDefinition = "INTEGER DEFAULT 0")
    private Integer githubFollowers;

    @Column(name = "github_following", columnDefinition = "INTEGER DEFAULT 0")
    private Integer githubFollowing;

    @Column(name = "github_created_at")
    private LocalDateTime githubCreatedAt;

    @Column(name = "current_streak", columnDefinition = "INTEGER DEFAULT 0")
    private Integer currentStreak;

    @Column(name = "longest_streak", columnDefinition = "INTEGER DEFAULT 0")
    private Integer longestStreak;

    @Column(name = "last_activity_date")
    private LocalDate lastActivityDate;

    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;

    @Column(name = "last_respec_at")
    private LocalDateTime lastRespecAt;

    @Enumerated(EnumType.STRING)
    @Column(name = "sync_status", nullable = false, columnDefinition = "VARCHAR(20) DEFAULT 'PENDING'")
    private SyncStatus syncStatus;

}


