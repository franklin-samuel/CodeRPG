package samukadev.coderpg.domain.github.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.github.GitHubEvent;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class StarEvent extends GitHubEvent {
    private String action;
    private Integer stargazersCount;

    @Override
    public void validate() {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Star action cannot be null or empty");
        }
    }
}
