package samukadev.coderpg.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubMobileAuthService {

    private final GitHubOAuthConfig oAuthConfig;
    private final WebClient.Builder webClientBuilder;

    public Map<String, Object> exchangeCodeForToken(String code, String redirectUri) {

        try {
            return webClientBuilder.build()
                    .post()
                    .uri("https://github.com/login/oauth/access_token")
                    .body(BodyInserters.fromFormData("client_id", oAuthConfig.getClientId())
                            .with("client_secret", oAuthConfig.getClientSecret())
                            .with("code", code)
                            .with("redirect_uri", redirectUri))
                    .header("Accept", "application/json")
                    .retrieve()
                    .bodyToMono(new  ParameterizedTypeReference<Map<String, Object>>(){})
                    .onErrorResume(error -> {
                        if (error instanceof WebClientResponseException e) {
                            log.error("Corpo do erro do Github: " + e.getResponseBodyAsString());
                        }

                        return Mono.error(new BusinessException(
                                "Failed to authenticate with Github: " + error.getMessage()
                        ));
                    })
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Github authentication failed: " + e.getMessage());
        }
    }

    public Map<String, Object> getGitHubUser(String accessToken) {
        try {
            return webClientBuilder.build()
                    .get()
                    .uri("https://api.github.com/user")
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Accept", "application/vnd.github.v3+json")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {})
                    .onErrorResume(error -> {
                        return Mono.error(new BusinessException(
                                "Failed to get user from Github: " + error.getMessage()
                        ));
                    })
                    .block();
        } catch (Exception e) {
            throw new BusinessException("Failed to get Github user: " + e.getMessage());
        }
    }

    public LocalDateTime calculateExpiresAt(Object expiresIn) {
        if (expiresIn == null) {
            return LocalDateTime.now().plusHours(8);
        }

        int seconds = expiresIn instanceof Number
                ? ((Number) expiresIn).intValue()
                : 28800;

        return LocalDateTime.now().plusSeconds(seconds);
    }

}
