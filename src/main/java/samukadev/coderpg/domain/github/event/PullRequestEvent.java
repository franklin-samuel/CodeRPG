package samukadev.coderpg.domain.github.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import samukadev.coderpg.domain.github.GitHubEvent;
import samukadev.coderpg.domain.github.model.GitHubPullRequest;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PullRequestEvent extends GitHubEvent {
    private String action;
    private GitHubPullRequest pullRequest;
    private Boolean wasMerged;

    @Override
    public void validate() {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Pull request action cannot be null or empty");
        }
        if (pullRequest == null) {
            throw new IllegalArgumentException("Pull request data cannot be null");
        }
    }
}
