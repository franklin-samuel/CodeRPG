package samukadev.coderpg.domain.github;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.GitHubEventType;

import java.time.LocalDateTime;

@Data
@SuperBuilder
@NoArgsConstructor
public abstract class GitHubEvent {
    private String eventId;
    private GitHubEventType eventType;
    private String repositoryFullName;
    private String senderUsername;
    private Long senderGithubId;
    private LocalDateTime occurredAt;

    public abstract void validate();
}
