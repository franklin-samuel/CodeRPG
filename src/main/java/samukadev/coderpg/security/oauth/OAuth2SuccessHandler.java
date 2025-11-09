package samukadev.coderpg.security.oauth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.CreateUserPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.enums.SyncStatus;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepositoryPort userRepository;
    private final CreateUserPort createUserPort;
    private final GitHubOAuthService gitHubOAuthService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        // Get GitHub user info
        Long githubId = ((Number) attributes.get("id")).longValue();
        String githubUsername = (String) attributes.get("login");
        String name = (String) attributes.get("name");
        String email = (String) attributes.get("email");
        String avatarUrl = (String) attributes.get("avatar_url");
        String bio = (String) attributes.get("bio");
        String location = (String) attributes.get("location");
        String website = (String) attributes.get("blog");
        Integer publicRepos = (Integer) attributes.get("public_repos");
        Integer followers = (Integer) attributes.get("followers");
        Integer following = (Integer) attributes.get("following");
        String createdAt = (String) attributes.get("created_at");

        // Get OAuth token
        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String token = accessToken.getTokenValue();
        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;

        // Check if user exists
        Optional<User> existingUser = userRepository.findByEmail(email != null ? email : githubUsername + "@github.com");

        User user;
        if (existingUser.isPresent()) {
            // Update existing user
            user = existingUser.get();
            user.setGithubAccessToken(token);
            user.setGithubRefreshToken(refreshToken);
            user.setTokenExpiresAt(
                    accessToken.getExpiresAt() != null
                            ? LocalDateTime.ofInstant(accessToken.getExpiresAt(), ZoneId.systemDefault())
                            : null
            );
            user.setAvatarUrl(avatarUrl);
            user.setBio(bio);
            user.setLocation(location);
            user.setWebsite(website);
            user.setGithubPublicRepos(publicRepos);
            user.setGithubFollowers(followers);
            user.setGithubFollowing(following);
            user.setLastSyncAt(LocalDateTime.now());
            user.setSyncStatus(SyncStatus.COMPLETED);

            userRepository.save(user);
            log.info("Updated existing user: {}", githubUsername);
        } else {
            // Create new user
            User newUser = User.builder()
                    .githubId(githubId)
                    .githubUsername(githubUsername)
                    .name(name)
                    .email(email != null ? email : githubUsername + "@github.com")
                    .avatarUrl(avatarUrl)
                    .bio(bio)
                    .location(location)
                    .website(website)
                    .classType(ClassType.FULLSTACK)
                    .level(1)
                    .xp(0)
                    .totalXp(0L)
                    .githubPublicRepos(publicRepos)
                    .githubFollowers(followers)
                    .githubFollowing(following)
                    .githubCreatedAt(createdAt != null ? LocalDateTime.parse(createdAt.replace("Z", "")) : null)
                    .currentStreak(0)
                    .longestStreak(0)
                    .lastActivityDate(null)
                    .lastSyncAt(LocalDateTime.now())
                    .lastRespecAt(null)
                    .syncStatus(SyncStatus.COMPLETED)
                    .githubAccessToken(token)
                    .githubRefreshToken(refreshToken)
                    .tokenExpiresAt(
                            accessToken.getExpiresAt() != null
                                    ? LocalDateTime.ofInstant(accessToken.getExpiresAt(), ZoneId.systemDefault())
                                    : null
                    )
                    .active(true)
                    .build();

            Context context = new Context(newUser);
            user = createUserPort.execute(context);
            log.info("Created new user: {}", githubUsername);
        }

        // Redirect to dashboard or home
        getRedirectStrategy().sendRedirect(request, response, "/dashboard");
    }

}