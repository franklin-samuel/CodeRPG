package samukadev.coderpg.infrastructure.github.exceptions;

import lombok.Getter;

@Getter
public class GitHubApiException extends RuntimeException {

    private final int statusCode;

    public GitHubApiException(String message) {
        super(message);
        this.statusCode = 500;
    }

    public GitHubApiException(String message, int statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

    public GitHubApiException(String message, Throwable cause) {
        super(message, cause);
        this.statusCode = 500;
    }

    public GitHubApiException(String message, int statusCode, Throwable cause) {
        super(message, cause);
        this.statusCode = statusCode;
    }

}

