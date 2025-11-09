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
public class ForkEvent extends GitHubEvent {
    private String forkeeFullName;
    private Long forkeeId;
    private Integer forksCount;

    @Override
    public void validate() {
        if (forkeeFullName == null || forkeeFullName.isBlank()) {
            throw new IllegalArgumentException("Forkee full name cannot be null or empty");
        }
    }
}
