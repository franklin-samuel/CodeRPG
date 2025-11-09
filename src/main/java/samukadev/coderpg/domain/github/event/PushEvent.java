package samukadev.coderpg.domain.github.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.github.GitHubEvent;
import samukadev.coderpg.domain.github.model.GitHubCommit;

import java.util.ArrayList;
import java.util.List;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PushEvent extends GitHubEvent {
    private String ref;
    private String before;
    private String after;
    private List<GitHubCommit> commits = new ArrayList<>();
    private Integer commitsCount;
    private Boolean isForce;

    @Override
    public void validate() {
        if (ref == null || ref.isBlank()) {
            throw new IllegalArgumentException("Push event ref cannot be null or empty");
        }
        if (commits == null) {
            commits = new ArrayList<>();
        }
    }
}
