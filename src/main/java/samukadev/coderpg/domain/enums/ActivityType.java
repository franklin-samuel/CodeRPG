package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum ActivityType {
    LEVEL_UP("Level Up"),
    SKILL_LEVEL_UP("Skill Level Up"),
    MISSION_COMPLETE("Mission Complete"),
    STREAK_MILESTONE("Streak Milestone"),
    CLASS_CHANGED("Class Changed"),
    BUILD_CHANGED("Build Changed"),
    REPO_CREATED("Repo Created"),
    PR_MERGED("PR Merged"),
    FOLLOWED_USER("Followed User");

    private final String displayName;

    ActivityType(String displayName) {
        this.displayName = displayName;
    }
}
