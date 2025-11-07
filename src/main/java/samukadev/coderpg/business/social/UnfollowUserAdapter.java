package samukadev.coderpg.business.social;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.social.UnfollowUserPort;
import samukadev.coderpg.core.persistence.UserFollowRepositoryPort;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.persistence.repository.UserFollowRepository;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UnfollowUserAdapter implements UnfollowUserPort {

    private final UserFollowRepositoryPort repository;

    @Override
    public UserFollow execute(final Context context) {
        UUID followerId = context.getProperty("followerId", UUID.class);
        UUID followingId = context.getProperty("followingId", UUID.class);

        UserFollow userFollow = repository
                .findByFollowerIdAndFollowingId(followerId, followingId)
                .orElseThrow(() -> new BusinessException("Follow relationship not found"));

        repository.deleteByFollowerIdAndFollowingId(followerId, followingId);

        return userFollow;
    }

}
