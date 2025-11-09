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
public class GitHubPullRequest {
    private Long id;
    private Integer number;
    private String title;
    private String body;
    private String state;
    private Boolean merged;
    private String baseBranch;
    private String headBranch;
    private String userLogin;
    private Integer additions;
    private Integer deletions;
    private Integer changedFiles;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime mergedAt;
    private LocalDateTime closedAt;
}