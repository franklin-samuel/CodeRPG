package samukadev.coderpg.core.integration.github.event;

import samukadev.coderpg.core.Command;
import samukadev.coderpg.domain.github.GitHubEvent;

public interface GitHubEventProcessorPort<T extends GitHubEvent> extends Command<Boolean> {
    Boolean processEvent(T event);
}
