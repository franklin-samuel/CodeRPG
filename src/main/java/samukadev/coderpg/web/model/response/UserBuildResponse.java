package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBuildResponse {

    private UUID id;

    private String primaryLanguage;
    private Integer primaryLanguageLevel;
    private Integer primaryLanguageXp;

    private String secondaryLanguage;
    private Integer secondaryLanguageLevel;
    private Integer secondaryLanguageXp;

    private String framework;
    private String database;
    private String cloud;
    private String tool1;
    private String tool2;

}
