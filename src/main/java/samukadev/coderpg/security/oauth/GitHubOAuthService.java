package samukadev.coderpg.security.oauth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.stereotype.Service;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class GitHubOAuthService {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepositoryPort userRepository;

    public void saveUserToken(String githubUsername, String accessToken, String refreshToken, Instant expiresAt) {
        Optional<User> userOpt = userRepository.findByEmail(githubUsername + "@github.com");

        if (userOpt.isEmpty()) {
            log.warn("User not found for GitHub username: {}", githubUsername);
            return;
        }

        User user = userOpt.get();
        user.setGithubAccessToken(accessToken);
        user.setGithubRefreshToken(refreshToken);

        if (expiresAt != null) {
            user.setTokenExpiresAt(LocalDateTime.ofInstant(expiresAt, ZoneId.systemDefault()));
        }

        userRepository.save(user);
        log.info("GitHub token saved for user: {}", githubUsername);
    }

    public String getUserToken(String githubUsername) {
        Optional<User> userOpt = userRepository.findByEmail(githubUsername + "@github.com");

        if (userOpt.isEmpty()) {
            throw new BusinessException("User not found: " + githubUsername);
        }

        User user = userOpt.get();

        if (user.getGithubAccessToken() == null) {
            throw new BusinessException("User has no GitHub token. Please login again.");
        }

        // Check if token is expired
        if (user.getTokenExpiresAt() != null &&
                user.getTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BusinessException("GitHub token expired. Please login again.");
        }

        return user.getGithubAccessToken();
    }

    public OAuth2AuthorizedClient getAuthorizedClient(String principalName) {
        return authorizedClientService.loadAuthorizedClient("github", principalName);
    }

    public void revokeUserToken(String githubUsername) {
        Optional<User> userOpt = userRepository.findByEmail(githubUsername + "@github.com");

        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        user.setGithubAccessToken(null);
        user.setGithubRefreshToken(null);
        user.setTokenExpiresAt(null);

        userRepository.save(user);
        log.info("GitHub token revoked for user: {}", githubUsername);
    }

}