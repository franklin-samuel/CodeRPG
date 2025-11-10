package samukadev.coderpg.web.mappers;

import org.springframework.stereotype.Component;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.web.model.request.UpdateUserBuildRequest;
import samukadev.coderpg.web.model.response.UserBuildResponse;
import samukadev.coderpg.web.model.response.UserResponse;
import samukadev.coderpg.web.model.response.UserStatsResponse;

@Component
public class UserModelMapper {

    public UserResponse toResponse(User user) {
        if (user == null) return null;

        return UserResponse.builder()
                .id(user.getId())
                .githubId(user.getGithubId())
                .githubUsername(user.getGithubUsername())
                .name(user.getName())
                .email(user.getEmail())
                .avatarUrl(user.getAvatarUrl())
                .bio(user.getBio())
                .location(user.getLocation())
                .website(user.getWebsite())
                .classType(user.getClassType())
                .classDisplayName(user.getClassType() != null ? user.getClassType().getDisplayName() : null)
                .level(user.getLevel())
                .xp(user.getXp())
                .totalXp(user.getTotalXp())
                .githubPublicRepos(user.getGithubPublicRepos())
                .githubFollowers(user.getGithubFollowers())
                .githubFollowing(user.getGithubFollowing())
                .currentStreak(user.getCurrentStreak())
                .longestStreak(user.getLongestStreak())
                .lastActivityDate(user.getLastActivityDate())
                .followersCount(user.getFollowers() != null ? user.getFollowers().size() : 0)
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .lastSyncAt(user.getLastSyncAt())
                .syncStatus(user.getSyncStatus())
                .createdAt(user.getCreatedAt())
                .modifiedAt(user.getModifiedAt())
                .build();
    }

    public UserBuildResponse toBuildResponse(UserBuild build) {
        if (build == null) return null;

        return UserBuildResponse.builder()
                .id(build.getId())
                .primaryLanguage(build.getPrimaryLanguage())
                .primaryLanguageLevel(build.getPrimaryLanguageLevel())
                .primaryLanguageXp(build.getPrimaryLanguageXp())
                .secondaryLanguage(build.getSecondaryLanguage())
                .secondaryLanguageLevel(build.getSecondaryLanguageLevel())
                .secondaryLanguageXp(build.getSecondaryLanguageXp())
                .framework(build.getFramework())
                .database(build.getDatabase())
                .cloud(build.getCloud())
                .tool1(build.getTool1())
                .tool2(build.getTool2())
                .build();
    }

    public UserBuild toBuildDomain(UpdateUserBuildRequest request) {
        if (request == null) return null;

        return UserBuild.builder()
                .primaryLanguage(request.getPrimaryLanguage())
                .secondaryLanguage(request.getSecondaryLanguage())
                .framework(request.getFramework())
                .database(request.getDatabase())
                .cloud(request.getCloud())
                .tool1(request.getTool1())
                .tool2(request.getTool2())
                .build();
    }

    public UserStatsResponse toStatsResponse(User user, Context context) {
        if (user == null) return null;

        int xpToNextLevel = calculateXpToNextLevel(user.getLevel(), user.getXp());

        Integer totalSkills = context.getProperty("totalSkills", Integer.class);
        Long equippedSkillsLong = context.getProperty("equippedSkills", Long.class);
        Integer equippedSkills = equippedSkillsLong != null ? equippedSkillsLong.intValue() : 0;

        return UserStatsResponse.builder()
                .level(user.getLevel())
                .xp(user.getXp())
                .xpToNextLevel(xpToNextLevel)
                .totalXp(user.getTotalXp())
                .currentStreak(user.getCurrentStreak())
                .longestStreak(user.getLongestStreak())
                .totalSkills(totalSkills != null ? totalSkills : 0)
                .equippedSkills(equippedSkills)
                .followersCount(user.getFollowers() != null ? user.getFollowers().size() : 0)
                .followingCount(user.getFollowing() != null ? user.getFollowing().size() : 0)
                .githubPublicRepos(user.getGithubPublicRepos())
                .githubFollowers(user.getGithubFollowers())
                .build();
    }

    private int calculateXpToNextLevel(Integer level, Integer currentXp) {
        if (level == null || currentXp == null) return 0;
        int nextLevelTotalXp = (level + 1) * (level + 1) * 100;
        int currentLevelTotalXp = level * level * 100;
        return nextLevelTotalXp - (currentLevelTotalXp + currentXp);
    }

}
