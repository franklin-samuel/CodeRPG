package samukadev.coderpg.persistence.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "github_token")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
public class GitHubTokenEntity extends AbstractEntity<UUID> {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "UUID")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true,
            foreignKey = @ForeignKey(name = "fk_user_build_user"))
    private UserEntity user;

    @Column(name = "github_access_token", nullable = false)
    private String githubAccessToken;

    @Column(name = "github_refresh_token", nullable = false)
    private String githubRefreshToken;

    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

}
