package samukadev.coderpg.domain.github.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GitHubRepository {
    private Long id;
    private String name;
    private String fullName;
    private String description;
    private Boolean isPrivate;
    private Boolean isFork;
    private String language;
    private Integer stargazersCount;
    private Integer forksCount;
    private Integer openIssuesCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime pushedAt;
}