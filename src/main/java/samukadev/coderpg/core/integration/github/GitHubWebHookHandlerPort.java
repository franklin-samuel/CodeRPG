package samukadev.coderpg.core.integration.github;

import samukadev.coderpg.core.Command;
import samukadev.coderpg.domain.github.GitHubWebhookPayload;

public interface GitHubWebHookHandlerPort extends Command<Boolean> {

    Boolean processWebhook(GitHubWebhookPayload payload, String eventType);

}
