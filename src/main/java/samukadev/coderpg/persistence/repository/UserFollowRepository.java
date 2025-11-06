package samukadev.coderpg.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import samukadev.coderpg.persistence.model.UserFollowEntity;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserFollowRepository extends JpaRepository<UserFollowEntity, UUID> {

    List<UserFollowEntity> findByFollowerId(UUID followerId);

    List<UserFollowEntity> findByFollowingId(UUID followingId);

    Optional<UserFollowEntity> findByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    boolean existsByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

    long countByFollowerId(UUID followerId);

    long countByFollowingId(UUID followingId);

    void deleteByFollowerIdAndFollowingId(UUID followerId, UUID followingId);

}
