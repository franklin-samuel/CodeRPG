package samukadev.coderpg.infrastructure.github.exceptions;

public class GitHubWebhookException extends RuntimeException {

    public GitHubWebhookException(String message) {
        super(message);
    }

    public GitHubWebhookException(String message, Throwable cause) {
        super(message, cause);
    }

}
