package samukadev.coderpg.infrastructure.github.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "github")
public class GitHubProperties {

    private String apiUrl;
    private String appId;
    private String webhookSecret;
    private Integer requestTimeout = 30000;
    private Integer maxRetries = 3;
    private Boolean enableWebhooks = true;

}
