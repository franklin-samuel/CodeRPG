package samukadev.coderpg.domain.enums;

import lombok.Getter;

@Getter
public enum ClassType {
    FRONTEND("Frontend Mage"),
    BACKEND("Backend Knight"),
    FULLSTACK("Full Stack Warrior"),
    DATA_ENGINEER("Data Elf"),
    DEVOPS("DevOps Assassin"),
    MOBILE("Mobile Ranger");


    private final String displayName;

    ClassType(String displayName) {
        this.displayName = displayName;
    }
}
