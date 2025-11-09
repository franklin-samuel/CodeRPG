package samukadev.coderpg.domain.github;

import samukadev.coderpg.domain.enums.GitHubEventType;

import java.time.LocalDateTime;

public abstract class GitHubEvent {
    private String eventId;
    private GitHubEventType eventType;
    private String repositoryFullName;
    private String senderUsername;
    private Long senderGithubId;
    private LocalDateTime occurredAt;

    public abstract void validate();
}
