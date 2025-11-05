package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum SkillType {
    PRIMARY_LANGUAGE("Primary Language"),
    SECONDARY_LANGUAGE("Secondary Language"),
    FRAMEWORK("Framework"),
    DATABASE("Database"),
    CLOUD("Cloud"),
    TOOL1("Tool 1"),
    TOOL2("Tool 2"),;

    private final String displayName;

    SkillType(String displayName) {
        this.displayName = displayName;
    }
}
