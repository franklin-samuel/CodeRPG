package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum DailyMissionType {
    DAILY_COMMIT_1(
            "Make 1 commit",
            "Push at least 1 commit today",
            50,
            Difficulty.EASY,
            "💾",
            1
    ),
    DAILY_COMMIT_3(
            "Make 3 commits",
            "Push at least 3 commits today",
            100,
            Difficulty.NORMAL,
            "💾",
            3
    ),
    DAILY_COMMIT_5(
            "Commit Champion",
            "Push at least 5 commits today",
            200,
            Difficulty.HARD,
            "💾",
            5
    ),
    DAILY_PR_1(
            "Open a PR",
            "Open at least 1 Pull Request",
            75,
            Difficulty.NORMAL,
            "🔀",
            1
    ),
    DAILY_ISSUE_1(
            "Report an Issue",
            "Open at least 1 issue",
            50,
            Difficulty.EASY,
            "🐛",
            1
    ),
    DAILY_REVIEW_1(
            "Code Review",
            "Review at least 1 PR",
            60,
            Difficulty.NORMAL,
            "👀",
            1
    ),
    DAILY_STAR_1(
            "Give a Star",
            "Star at least 1 repository",
            25,
            Difficulty.EASY,
            "⭐",
            1
    );

    private final String title;
    private final String description;
    private final int rewardXp;
    private final Difficulty difficulty;
    private final String icon;
    private final int target;

    DailyMissionType(String title, String description, int rewardXp, Difficulty difficulty, String icon, int target) {
        this.title = title;
        this.description = description;
        this.rewardXp = rewardXp;
        this.difficulty = difficulty;
        this.icon = icon;
        this.target = target;
    }
}