package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum MissionType {
    DAILY("Daily"),
    WEEKLY("Weekly");

    private final String displayName;

    MissionType(String displayName) {
        this.displayName = displayName;
    }
}
