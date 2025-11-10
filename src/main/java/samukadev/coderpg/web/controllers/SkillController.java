package samukadev.coderpg.web.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.skill.EquipSkillPort;
import samukadev.coderpg.core.business.skill.GetSkillHistoryPort;
import samukadev.coderpg.core.business.skill.SaveSkillProgressPort;
import samukadev.coderpg.core.business.skill.UnequipSkillPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.web.commons.ApiResponse;
import samukadev.coderpg.web.mappers.SkillModelMapper;
import samukadev.coderpg.web.model.request.EquipSkillRequest;
import samukadev.coderpg.web.model.request.SaveSkillProgressRequest;
import samukadev.coderpg.web.model.response.SkillHistoryResponse;
import samukadev.coderpg.web.model.response.SkillResponse;
import samukadev.coderpg.web.routes.SkillsRoute;
import samukadev.coderpg.web.security.SecurityUtils;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final GetSkillHistoryPort getSkillHistoryPort;
    private final EquipSkillPort equipSkillPort;
    private final UnequipSkillPort unequipSkillPort;
    private final SaveSkillProgressPort saveSkillProgressPort;
    private final SkillModelMapper skillModelMapper;
    private final SecurityUtils securityUtils;

    @GetMapping(SkillsRoute.HISTORY)
    public ResponseEntity<ApiResponse<SkillHistoryResponse>> getSkillHistory(
            @AuthenticationPrincipal OAuth2User principal
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);

        getSkillHistoryPort.execute(context);

        @SuppressWarnings("unchecked")
        List<SkillHistory> skills = (List<SkillHistory>) context.get("skillsList");
        Integer totalSkills = context.getProperty("totalSkills", Integer.class);
        Long equippedSkillsCount = context.getProperty("equippedSkills", Long.class);

        List<SkillResponse> skillResponses = skills.stream()
                .map(skillModelMapper::toResponse)
                .toList();

        List<SkillResponse> equippedSkills = skillResponses.stream()
                .filter(SkillResponse::getIsEquipped)
                .toList();

        SkillHistoryResponse response = SkillHistoryResponse.builder()
                .skills(skillResponses)
                .totalSkills(totalSkills)
                .equippedSkills(equippedSkillsCount.intValue())
                .equippedSkillsList(equippedSkills)
                .build();

        return ResponseEntity.ok(ApiResponse.success(response));
    }

    @GetMapping(SkillsRoute.BY_TYPE)
    public ResponseEntity<ApiResponse<SkillResponse>> getSkillByType(
            @AuthenticationPrincipal OAuth2User principal,
            @PathVariable SkillType type
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);

        getSkillHistoryPort.execute(context);

        @SuppressWarnings("unchecked")
        List<SkillHistory> skills = (List<SkillHistory>) context.get("skillsList");

        List<SkillResponse> filteredSkills = skills.stream()
                .filter(skill -> skill.getSkillType() == type)
                .map(skillModelMapper::toResponse)
                .toList();

        return ResponseEntity.ok(ApiResponse.success((SkillResponse) filteredSkills));
    }

    @PostMapping(SkillsRoute.EQUIP)
    public ResponseEntity<ApiResponse<SkillResponse>> equipSkill(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody EquipSkillRequest request
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);
        context.putProperty("skillType", request.getSkillType());
        context.putProperty("skillName", request.getSkillName());

        SkillHistory skill = equipSkillPort.execute(context);
        SkillResponse response = skillModelMapper.toResponse(skill);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Skill equipped successfully"
        ));
    }

    @PostMapping(SkillsRoute.UNEQUIP)
    public ResponseEntity<ApiResponse<SkillResponse>> unequipSkill(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody EquipSkillRequest request
    ) {
        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);
        context.putProperty("skillType", request.getSkillType());
        context.putProperty("skillName", request.getSkillName());

        SkillHistory skill = unequipSkillPort.execute(context);
        SkillResponse response = skillModelMapper.toResponse(skill);

        return ResponseEntity.ok(ApiResponse.success(
                response,
                "Skill unequipped successfully"
        ));
    }

    @PostMapping(SkillsRoute.ROOT + "/progress")
    public ResponseEntity<ApiResponse<SkillResponse>> saveSkillProgress(
            @AuthenticationPrincipal OAuth2User principal,
            @Valid @RequestBody SaveSkillProgressRequest request
    ) {

        UUID userId = securityUtils.getAuthenticatedUserId(principal);

        Context context = new Context();
        context.putProperty("userId", userId);
        context.putProperty("skillType", request.getSkillType());
        context.putProperty("skillName", request.getSkillName());
        context.putProperty("xpToAdd", request.getXpToAdd());

        SkillHistory skill = saveSkillProgressPort.execute(context);
        SkillResponse response = skillModelMapper.toResponse(skill);

        Boolean leveledUp = context.getProperty("leveledUp", Boolean.class);
        String message = leveledUp != null && leveledUp
                ? "Skill leveled up!"
                : "Skill progress saved";

        return ResponseEntity.ok(ApiResponse.success(response, message));
    }

}
