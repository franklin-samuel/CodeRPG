package samukadev.coderpg.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserBuild extends AbstractDomain {

    private UUID userId;

    private String primaryLanguage;
    private int primaryLanguageLevel;
    private int primaryLanguageXp;

    private String  secondaryLanguage;
    private int secondaryLanguageLevel;
    private int secondaryLanguageXp;

    private String framework;
    private String database;
    private String cloud;
    private String tool1;
    private String tool2;

}
