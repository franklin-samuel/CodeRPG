package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum XpSource {
    COMMIT("Commit"),
    PR_OPENED("PR Opened"),
    PR_MERGED("PR Merged"),
    ISSUE_OPENED("Issue Opened"),
    ISSUE_CLOSED("Issue Closed"),
    REPO_CREATED("Repo Created"),
    STAR_RECEIVED("Star Received"),
    FORK_RECEIVED("Fork Received"),
    MISSION_DAILY("Mission Daily"),
    MISSION_WEEKLY("Mission Weekly"),
    MANUAL_ADJUSTMENT("Manual Adjustment"),;

    private final String displayName;

    XpSource(String displayName) {
        this.displayName = displayName;
    }
}
