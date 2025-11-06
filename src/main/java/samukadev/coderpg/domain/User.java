package samukadev.coderpg.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.enums.SyncStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractDomain {

    private List<UserFollow> following = new ArrayList<>();

    private List<UserFollow> followers = new ArrayList<>();

    private Long githubId;
    private String githubUsername;

    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private String location;
    private String website;

    private ClassType classType;

    private Integer level;
    private Integer xp;
    private Long totalXp;

    private Integer githubPublicRepos;
    private Integer githubFollowers;
    private Integer githubFollowing;
    private LocalDateTime githubCreatedAt;

    private Integer currentStreak;
    private Integer longestStreak;
    private LocalDate lastActivityDate;

    private LocalDateTime lastSyncAt;
    private LocalDateTime lastRespecAt;
    private SyncStatus syncStatus;

}
