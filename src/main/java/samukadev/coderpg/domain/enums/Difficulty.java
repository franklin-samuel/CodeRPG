package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum Difficulty {
    EASY("Easy"),
    NORMAL("Normal"),
    HARD("Hard");

    private final String displayName;

    Difficulty(String displayName) {
        this.displayName = displayName;
    }
}
