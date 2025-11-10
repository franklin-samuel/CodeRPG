package samukadev.coderpg.web.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.mission.CompleteMissionPort;
import samukadev.coderpg.core.business.mission.GenerateDailyMissionsPort;
import samukadev.coderpg.core.persistence.UserMissionRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.domain.enums.MissionType;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.mappers.MissionModelMapper;
import samukadev.coderpg.web.model.response.MissionListResponse;
import samukadev.coderpg.web.routes.MissionsRoute;
import samukadev.coderpg.web.model.response.MissionResponse;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class MissionController {

    private final GenerateDailyMissionsPort generateDailyMissionsPort;
    private final CompleteMissionPort completeMissionPort;
    private final UserMissionRepositoryPort userMissionRepository;
    private final UserRepositoryPort userRepository;
    private final MissionModelMapper missionDtoMapper;

    @GetMapping(MissionsRoute.DAILY)
    public ResponseEntity<ApiResponse<MissionListResponse>> getDailyMissions(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        Long githubId = principal.getAttribute("id");
        User user = userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Context context = new Context();
        context.putProperty("userId", user.getId());

        generateDailyMissionsPort.execute(context);

        List<UserMission> missions = userMissionRepository
                .findByUserIdAndMissionType(user.getId(), MissionType.DAILY);

        return buildMissionListResponse(missions);
    }

    @GetMapping(MissionsRoute.ACTIVE)
    public ResponseEntity<ApiResponse<MissionListResponse>> getActiveMissions(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        Long githubId = principal.getAttribute("id");
        User user = userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));

        List<UserMission> missions = userMissionRepository
                .findActiveByUserId(user.getId());

        return buildMissionListResponse(missions);
    }

    @GetMapping(MissionsRoute.ROOT)
    public ResponseEntity<ApiResponse<MissionListResponse>> getAllMissions(
            @AuthenticationPrincipal OAuth2User principal
    ) {

        Long githubId = principal.getAttribute("id");
        User user = userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));

        List<UserMission> missions = userMissionRepository
                .findByUserId(user.getId());

        return buildMissionListResponse(missions);
    }

    @GetMapping(MissionsRoute.BY_ID)
    public ResponseEntity<ApiResponse<MissionResponse>> getMissionById(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable UUID id
    ) {

        Long githubId = principal.getAttribute("id");
        User user = userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));

        UserMission mission = userMissionRepository.get(id)
                .orElseThrow(() -> new BusinessException("Mission not found"));

        if (!mission.getUserId().equals(user.getId())) {
            throw new BusinessException("Mission does not belong to user");
        }

        MissionResponse response = missionDtoMapper.toResponse(mission);

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @PostMapping(MissionsRoute.COMPLETE)
    public ResponseEntity<ApiResponse<MissionResponse>> completeMission(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable UUID id
    ) {

        Long githubId = principal.getAttribute("id");
        User user = userRepository.findByGitHubId(githubId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Context context = new Context();
        context.putProperty("userId", user.getId());
        context.putProperty("missionId", id);

        UserMission mission = completeMissionPort.execute(context);
        MissionResponse response = missionDtoMapper.toResponse(mission);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Mission completed! +" + mission.getRewardXp() + " XP"
        ));
    }

    private ResponseEntity<ApiResponse<MissionListResponse>> buildMissionListResponse(
            List<UserMission> missions
    ) {
        List<MissionResponse> missionResponses = missions.stream()
                .map(missionDtoMapper::toResponse)
                .toList();

        int completedCount = (int) missions.stream()
                .filter(UserMission::getCompleted)
                .count();

        int activeCount = (int) missions.stream()
                .filter(m -> !m.getCompleted())
                .count();

        int totalXp = missions.stream()
                .filter(UserMission::getCompleted)
                .mapToInt(UserMission::getRewardXp)
                .sum();

        int potentialXp = missions.stream()
                .filter(m -> !m.getCompleted())
                .mapToInt(UserMission::getRewardXp)
                .sum();

        MissionListResponse response = MissionListResponse.builder()
                .missions(missionResponses)
                .totalMissions(missions.size())
                .completedMissions(completedCount)
                .activeMissions(activeCount)
                .totalXpEarned(totalXp)
                .potentialXp(potentialXp)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }
}