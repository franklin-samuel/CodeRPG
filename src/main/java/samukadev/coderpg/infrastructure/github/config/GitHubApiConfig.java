package samukadev.coderpg.infrastructure.github.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.web.reactive.function.client.ServletOAuth2AuthorizedClientExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@RequiredArgsConstructor
public class GitHubApiConfig {

    private final GitHubProperties gitHubProperties;
    private final OAuth2AuthorizedClientManager authorizedClientManager;

    @Bean
    public WebClient gitHubWebClient() {
        ServletOAuth2AuthorizedClientExchangeFilterFunction oauth2 =
                new  ServletOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager);

        oauth2.setDefaultOAuth2AuthorizedClient(true);

        return WebClient.builder()
                .baseUrl(gitHubProperties.getApiUrl())
                .apply(oauth2.oauth2Configuration())
                .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github.v3+json")
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("User-Agent", "CodeRPG-App")
                .build();
    }

}
