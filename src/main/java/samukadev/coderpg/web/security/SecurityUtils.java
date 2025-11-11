package samukadev.coderpg.web.security;

import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepositoryPort userRepository;

    public User getAuthenticatedUser(OAuth2User principal) {
        if (principal == null) {
            throw new BusinessException("User not authenticated");
        }

        Object githubIdObj = principal.getAttribute("github_id");
        if (githubIdObj == null) {
            throw new BusinessException("Invalid authentication token");
        }
        Long githubId = ((Number) githubIdObj).longValue();

        return userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));
    }


    public void validateResourceOwner(OAuth2User principal, UUID resourceOwnerId) {
        User user = getAuthenticatedUser(principal);

        if (!user.getId().equals(resourceOwnerId)) {
            throw new BusinessException("Access denied: You don't have permission to access this resource");
        }
    }

    public boolean isResourceOwner(OAuth2User principal, UUID resourceOwnerId) {
        try {
            User user = getAuthenticatedUser(principal);
            return user.getId().equals(resourceOwnerId);
        } catch (BusinessException e) {
            return false;
        }
    }

    public UUID getAuthenticatedUserId(OAuth2User principal) {
        return getAuthenticatedUser(principal).getId();
    }
}