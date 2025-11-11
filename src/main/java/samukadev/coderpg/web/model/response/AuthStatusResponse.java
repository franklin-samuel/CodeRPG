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
public class AuthStatusResponse {

    private Boolean authenticated;
    private UUID userId;
    private String githubUsername;
    private String name;
    private String avatarUrl;
    private Boolean hasValidGitHubToken;
    private Boolean needsOnBoarding;

}
