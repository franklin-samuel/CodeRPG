package samukadev.coderpg.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.GetUserProfilePort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class GetUserProfileAdapter implements GetUserProfilePort {

    private final UserRepositoryPort repository;

    @Override
    public User execute(final Context context) {

        UUID userId = context.getProperty("userId",  UUID.class);

        if (userId == null) {
            throw new BusinessException("User ID is required");
        }

        User user = repository.get(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        context.putProperty("followersCount", user.getFollowers() != null ? user.getFollowers().size() : 0);
        context.putProperty("followingCount", user.getFollowing() != null ? user.getFollowing().size() : 0);

        return user;

    }

}
