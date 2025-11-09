package samukadev.coderpg.security.oauth;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.security.oauth2.client.registration.github")
public class GitHubOAuthConfig {

    private String clientId;
    private String clientSecret;
    private String[] scope;
    private String redirectUri;

}
