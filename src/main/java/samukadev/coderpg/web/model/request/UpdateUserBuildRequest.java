package samukadev.coderpg.web.model.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserBuildRequest {

    private String primaryLanguage;
    private String secondaryLanguage;
    private String framework;
    private String database;
    private String cloud;
    private String tool1;
    private String tool2;

}
