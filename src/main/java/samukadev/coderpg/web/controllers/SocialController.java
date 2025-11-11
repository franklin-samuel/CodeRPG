package samukadev.coderpg.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.social.FollowUserPort;
import samukadev.coderpg.core.business.social.UnfollowUserPort;
import samukadev.coderpg.core.persistence.UserFollowRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.mappers.UserModelMapper;
import samukadev.coderpg.web.model.response.UserResponse;
import samukadev.coderpg.web.routes.UsersRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SocialController {

    private final FollowUserPort followUserPort;
    private final UnfollowUserPort unfollowUserPort;
    private final UserFollowRepositoryPort userFollowRepositoryPort;
    private final UserRepositoryPort userRepository;
    private final UserModelMapper userModelMapper;
    private final SecurityUtils securityUtils;

    @PostMapping(UsersRoute.USER_FOLLOWERS)
    public ResponseEntity<ApiResponse<String>> followUser(
            @PathVariable UUID userId,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID currentUserId = securityUtils.getAuthenticatedUserId(principal);

        if (currentUserId.equals(userId)) {
            throw new BusinessException("You cannot follow yourself");
        }

        Context context = new Context();
        context.putProperty("followerId", currentUserId);
        context.putProperty("followingId", userId);

        followUserPort.execute(context);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User followed successfully"));
    }

    @DeleteMapping(UsersRoute.FOLLOW_RELATIONSHIP)
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @PathVariable UUID userId,
            @PathVariable UUID followerId,
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID currentUserId = securityUtils.getAuthenticatedUserId(principal);

        if (!currentUserId.equals(followerId)) {
            throw new BusinessException("You can only unfollow as yourself");
        }

        Context context = new Context();
        context.putProperty("followerId", followerId);
        context.putProperty("followingId", userId);

        unfollowUserPort.execute(context);

        return ResponseEntity.ok(ApiResponse.success("User unfollowed successfully"));
    }

    @GetMapping(UsersRoute.USER_FOLLOWERS)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowers(
            @PathVariable UUID userId
    ) {
        List<UserFollow> followers = userFollowRepositoryPort
                .findByFollowingId(userId);

        List<UserResponse> followerUsers = followers.stream()
                .map(UserFollow::getFollowerId)
                .map(id -> userRepository.get(id).orElse(null))
                .filter(Objects::nonNull)
                .map(userModelMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(followerUsers));
    }

    @GetMapping(UsersRoute.USER_FOLLOWING)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowing(
            @PathVariable UUID userId
    ) {
        List<UserFollow> following = userFollowRepositoryPort
                .findByFollowerId(userId);

        List<UserResponse> followingUsers = following.stream()
                .map(UserFollow::getFollowingId)
                .map(id -> userRepository.get(id).orElse(null))
                .filter(Objects::nonNull)
                .map(userModelMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(followingUsers));
    }
}