package samukadev.coderpg.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GitHubToken extends AbstractDomain {

    private UUID userId;
    private User user;
    private String githubAccessToken;
    private String githubRefreshToken;
    private LocalDateTime expiresAt;

}
