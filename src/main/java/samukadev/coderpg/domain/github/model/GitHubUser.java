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
public class GitHubUser {
    private Long id;
    private String login;
    private String name;
    private String email;
    private String avatarUrl;
    private String bio;
    private String location;
    private String blog;
    private Integer publicRepos;
    private Integer followers;
    private Integer following;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
