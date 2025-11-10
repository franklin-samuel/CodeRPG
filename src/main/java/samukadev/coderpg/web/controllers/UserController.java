package samukadev.coderpg.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.CompleteUserOnBoardingPort;
import samukadev.coderpg.core.business.user.GetUserProfilePort;
import samukadev.coderpg.core.business.user.UpdateUserBuildPort;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.mappers.UserModelMapper;
import samukadev.coderpg.web.model.request.CompleteOnBoardingRequest;
import samukadev.coderpg.web.model.request.UpdateUserBuildRequest;
import samukadev.coderpg.web.model.response.UserResponse;
import samukadev.coderpg.web.model.response.UserStatsResponse;
import samukadev.coderpg.web.routes.UsersRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final GetUserProfilePort getUserProfilePort;
    private final CompleteUserOnBoardingPort completeUserOnBoardingPort;
    private final UpdateUserBuildPort  updateUserBuildPort;
    private final UserModelMapper userModelMapper;
    private final SecurityUtils securityUtils;

    @GetMapping(UsersRoute.ME)
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);

        User userProfile = getUserProfilePort.execute(context);
        UserResponse response = userModelMapper.toResponse(userProfile);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(UsersRoute.BY_ID)
    public ResponseEntity<ApiResponse<UserResponse>> getUserById(
            @PathVariable UUID id
    ) {
        Context context = new Context();
        context.putProperty("userId", id);

        User user = getUserProfilePort.execute(context);
        UserResponse response = userModelMapper.toResponse(user);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PutMapping(UsersRoute.ONBOARDING)
    public ResponseEntity<ApiResponse<UserResponse>> completeOnBoarding(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody CompleteOnBoardingRequest request
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);
        context.putProperty("name", request.getName());
        context.putProperty("classType", request.getClassType());
        context.putProperty("email", request.getEmail());

        User updatedUser = completeUserOnBoardingPort.execute(context);
        UserResponse response = userModelMapper.toResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Onboarding completed successfully"
        ));
    }

    @PutMapping(UsersRoute.BUILD)
    public ResponseEntity<ApiResponse<UserResponse>> updateBuild(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody UpdateUserBuildRequest request
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        UserBuild buildData = userModelMapper.toBuildDomain(request);

        Context context = new Context();
        context.putProperty("userId", userId);
        context.putProperty("buildData", buildData);

        User updatedUser = updateUserBuildPort.execute(context);
        UserResponse response = userModelMapper.toResponse(updatedUser);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Build updated successfully"
        ));
    }

    @GetMapping(UsersRoute.STATS)
    public  ResponseEntity<ApiResponse<UserStatsResponse>> getUserStats(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);

        User userProfile = getUserProfilePort.execute(context);
        UserStatsResponse stats = userModelMapper.toStatsResponse(userProfile, context);

        return ResponseEntity.ok(ApiResponse.success(stats));
    }

}
