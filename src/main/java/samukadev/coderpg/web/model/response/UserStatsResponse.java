package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatsResponse {

    private Integer level;
    private Integer xp;
    private Integer xpToNextLevel;
    private Long totalXp;

    private Integer currentStreak;
    private Integer longestStreak;

    private Integer totalSkills;
    private Integer equippedSkills;

    private Integer completedMissions;
    private Integer activeMissions;

    private Integer followersCount;
    private Integer followingCount;

    private Integer githubPublicRepos;
    private Integer githubFollowers;

}