package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.enums.SyncStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private UUID id;
    private Long githubId;
    private String githubUsername;
    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private String location;
    private String website;

    private ClassType classType;
    private String classDisplayName;

    private Integer level;
    private Integer xp;
    private Long totalXp;

    private Integer githubPublicRepos;
    private Integer githubFollowers;
    private Integer githubFollowing;

    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;

    private Integer followersCount;
    private Integer followingCount;

    private LocalDateTime lastSyncAt;
    private SyncStatus syncStatus;

    private UserBuildResponse build;

    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

}
