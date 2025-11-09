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
public class GitHubCommit {
    private String sha;
    private String message;
    private String authorName;
    private String authorEmail;
    private LocalDateTime authorDate;
    private String committerName;
    private String committerEmail;
    private LocalDateTime committerDate;
    private String url;
}