package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum GitHubEventType {
    PUSH("push"),
    PULL_REQUEST("pull_request"),
    ISSUES("issues"),
    ISSUE_COMMENT("issue_comment"),
    STAR("star"),
    WATCH("watch"),
    FORK("fork"),
    CREATE("create"),
    DELETE("delete"),
    REPOSITORY("repository"),
    PULL_REQUEST_REVIEW("pull_request_review"),
    PULL_REQUEST_REVIEW_COMMENT("pull_request_review_comment"),
    RELEASE("release"),
    UNKNOWN("unknown");

    private final String value;

    GitHubEventType(String value) {
        this.value = value;
    }

    public static GitHubEventType fromValue(String value) {
        for (GitHubEventType type : values()) {
            if (type.value.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return UNKNOWN;
    }
}