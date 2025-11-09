package samukadev.coderpg.domain.github.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.github.GitHubEvent;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class IssueEvent extends GitHubEvent {
    private String action;
    private Long issueId;
    private Integer issueNumber;
    private String issueTitle;
    private String issueBody;
    private String issueState;

    @Override
    public void validate() {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Issue action cannot be null or empty");
        }
        if (issueNumber == null) {
            throw new IllegalArgumentException("Issue number cannot be null");
        }
    }
}
