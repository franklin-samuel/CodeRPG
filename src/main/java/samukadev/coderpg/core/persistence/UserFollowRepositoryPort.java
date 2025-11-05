package samukadev.coderpg.core.persistence;

import samukadev.coderpg.core.persistence.commons.BaseRepositoryPort;
import samukadev.coderpg.domain.UserFollow;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFollowRepositoryPort extends BaseRepositoryPort<UserFollow> {

    List<UserFollow> findByFollowerId(UUID followerId);

    List<UserFollow> findByFollowingId(UUID followingId);

    Optional<UserFollow> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

}
