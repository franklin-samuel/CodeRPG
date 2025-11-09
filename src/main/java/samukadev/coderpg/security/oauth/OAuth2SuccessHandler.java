package samukadev.coderpg.security.oauth;

import jakarta.servlet.ServletException;
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
import samukadev.coderpg.core.security.SaveGitHubTokenPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.ClassType;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final OAuth2AuthorizedClientService authorizedClientService;
    private final UserRepositoryPort userRepository;
    private final CreateUserPort createUserPort;
    private final SaveGitHubTokenPort saveGitHubTokenPort;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        if (!(authentication instanceof OAuth2AuthenticationToken oauthToken)) {
            super.onAuthenticationSuccess(request, response, authentication);
            return;
        }

        OAuth2User oAuth2User = oauthToken.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

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

        OAuth2AuthorizedClient authorizedClient = authorizedClientService.loadAuthorizedClient(
                oauthToken.getAuthorizedClientRegistrationId(),
                oauthToken.getName()
        );

        OAuth2AccessToken accessToken = authorizedClient.getAccessToken();
        String token = accessToken.getTokenValue();
        String refreshToken = authorizedClient.getRefreshToken() != null
                ? authorizedClient.getRefreshToken().getTokenValue()
                : null;
        LocalDateTime expiresAt = accessToken.getExpiresAt() != null
                ? LocalDateTime.ofInstant(accessToken.getExpiresAt(), ZoneId.systemDefault())
                : null;

        Optional<User> existingUser = userRepository.findByGitHubId(githubId);

        User user;
        boolean isNewUser = false;

        if (existingUser.isPresent()) {
            user = existingUser.get();
            user.setGithubUsername(githubUsername);
            user.setAvatarUrl(avatarUrl);
            user.setBio(bio);
            user.setLocation(location);
            user.setWebsite(website);
            user.setGithubPublicRepos(publicRepos);
            user.setGithubFollowers(followers);
            user.setGithubFollowing(following);
            user.setLastSyncAt(LocalDateTime.now());

            userRepository.save(user);
        } else {
            isNewUser = true;

            User newUser = User.builder()
                    .githubId(githubId)
                    .githubUsername(githubUsername)
                    .name(name)
                    .email(email)
                    .avatarUrl(avatarUrl)
                    .bio(bio)
                    .location(location)
                    .website(website)
                    .classType(ClassType.FULLSTACK)
                    .githubPublicRepos(publicRepos)
                    .githubFollowers(followers)
                    .githubFollowing(following)
                    .githubCreatedAt(createdAt != null ? LocalDateTime.parse(createdAt.replace("Z", "")) : null)
                    .active(true)
                    .build();

            Context createUserContext = new Context(newUser);
            user = createUserPort.execute(createUserContext);
        }

        Context saveTokenContext = new Context(user);
        saveTokenContext.putProperty("userId", user.getId());
        saveTokenContext.putProperty("accessToken", token);
        saveTokenContext.putProperty("refreshToken", refreshToken);
        saveTokenContext.putProperty("expiresAt", expiresAt);

        saveGitHubTokenPort.execute(saveTokenContext);

        String redirectUrl = isNewUser ? "/onboarding" : "/home";
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }





}