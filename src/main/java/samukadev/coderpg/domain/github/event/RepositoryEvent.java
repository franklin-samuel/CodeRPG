package samukadev.coderpg.domain.github.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.github.GitHubEvent;
import samukadev.coderpg.domain.github.model.GitHubRepository;

@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class RepositoryEvent extends GitHubEvent {
    private String action;
    private GitHubRepository repository;

    @Override
    public void validate() {
        if (action == null || action.isBlank()) {
            throw new IllegalArgumentException("Repository action cannot be null or empty");
        }
        if (repository == null) {
            throw new IllegalArgumentException("Repository data cannot be null");
        }
    }
}
