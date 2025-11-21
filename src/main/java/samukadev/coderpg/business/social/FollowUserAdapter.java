package samukadev.coderpg.business.social;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.social.FollowUserPort;
import samukadev.coderpg.core.persistence.UserFollowRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.persistence.repository.UserFollowRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class FollowUserAdapter implements FollowUserPort {

    private final UserFollowRepositoryPort followRepository;
    private final UserRepositoryPort userRepository;
    private final UserFollowRepositoryPort userFollowRepository;

    @Override
    public UserFollow execute(final Context context) {

        UUID followerId = context.getProperty("followerId",  UUID.class);
        UUID followingId = context.getProperty("followingId",  UUID.class);

        if (followerId.equals(followingId)) {
            throw new BusinessException("User can't follow themselves");
        }

        userRepository.get(followerId)
                .orElseThrow(() -> new BusinessException("Follower not found"));

        userRepository.get(followingId)
                .orElseThrow(() -> new BusinessException("Following not found"));

        if (userFollowRepository.existsByFollowerIdAndFollowingId(followerId, followingId)) {
            throw new BusinessException("Already following this user");
        }

        UserFollow userFollow = UserFollow.builder()
                .followerId(followerId)
                .followingId(followingId)
                .active(true)
                .build();

        return followRepository.save(userFollow);

    }

}
