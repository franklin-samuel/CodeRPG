package samukadev.coderpg.web.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.social.FollowUserPort;
import samukadev.coderpg.core.business.social.UnfollowUserPort;
import samukadev.coderpg.core.persistence.UserFollowRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.mappers.UserModelMapper;
import samukadev.coderpg.web.model.response.UserResponse;
import samukadev.coderpg.web.routes.SocialRoute;
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

    @PostMapping(SocialRoute.FOLLOW)
    public ResponseEntity<ApiResponse<String>> followUser(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable UUID userId
    ) {
        UUID currentUserId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("followerId", currentUserId);
        context.putProperty("followingId", userId);

        followUserPort.execute(context);

        return ResponseEntity.ok(ApiResponse.success("User followed successfully"));
    }

    @PostMapping(SocialRoute.UNFOLLOW)
    public ResponseEntity<ApiResponse<String>> unfollowUser(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable UUID userId
    ) {
        UUID currentUserId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("followerId", currentUserId);
        context.putProperty("followingId", userId);

        unfollowUserPort.execute(context);

        return ResponseEntity.ok(ApiResponse.success("User unfollowed successfully"));
    }

    @GetMapping(SocialRoute.FOLLOWERS)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowers(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        UUID currentUserId = securityUtils.getAuthenticatedUserId(principal);

        List<UserFollow> followers = userFollowRepositoryPort
                .findByFollowingId(currentUserId);

        List<UserResponse> followerUsers = followers.stream()
                .map(UserFollow::getFollowerId)
                .map(id -> userRepository.get(id).orElse(null))
                .filter(Objects::nonNull)
                .map(userModelMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(followerUsers));
    }

    @GetMapping(SocialRoute.FOLLOWING)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowing(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        UUID currentUserId =  securityUtils.getAuthenticatedUserId(principal);

        List<UserFollow> following = userFollowRepositoryPort
                .findByFollowerId(currentUserId);

        List<UserResponse> followingUsers = following.stream()
                .map(UserFollow::getFollowingId)
                .map(id -> userRepository.get(id).orElse(null))
                .filter(Objects::nonNull)
                .map(userModelMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success(followingUsers));
    }

    @GetMapping(SocialRoute.FOLLOWERS_BY_ID)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowersByUser(
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

    @GetMapping(SocialRoute.FOLLOWINGS_BY_ID)
    public ResponseEntity<ApiResponse<List<UserResponse>>> getFollowingByUser(
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
