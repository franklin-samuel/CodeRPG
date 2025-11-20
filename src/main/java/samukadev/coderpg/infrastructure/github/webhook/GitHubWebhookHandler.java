package samukadev.coderpg.infrastructure.github.webhook;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.exceptions.GitHubWebhookException;
import samukadev.coderpg.domain.github.GitHubEvent;
import samukadev.coderpg.domain.github.GitHubWebhookPayload;
import samukadev.coderpg.infrastructure.github.mappers.*;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitHubWebhookHandler {

    private final UserRepositoryPort userRepositoryPort;

    private final PushEventMapper pushEventMapper;
    private final PullRequestEventMapper pullRequestEventMapper;
    private final IssueEventMapper issueEventMapper;
    private final StarEventMapper starEventMapper;
    private final RepositoryEventMapper repositoryEventMapper;

    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2)
    )
    public Boolean processWebhook(GitHubWebhookPayload payload, String eventType) {
        try {
            GitHubEventType type = GitHubEventType.valueOf(eventType);
            Map<String, Object> payloadMap = payload.getPayload();

            @SuppressWarnings("unchecked")
            Map<String, Object> sender = (Map<String, Object>) payloadMap.get("sender");

            if (sender == null) {
                return false;
            }

            Long senderGitHubId = ((Number) sender.get("id")).longValue();

            Optional<User> userOpt = userRepositoryPort.findByGitHubId(senderGitHubId);

            if(userOpt.isEmpty()){
                return false;
            }

            User user = userOpt.get();

            GitHubEvent event = mapEvent(type, payloadMap);

            if (event == null) {
                return false;
            }

            event.validate();

            return true;
        } catch (Exception e) {
            throw new GitHubWebhookException("Webhook processing failed", e);
        }
    }

    private GitHubEvent mapEvent(GitHubEventType type, Map<String, Object> payload) {
        return switch (type) {
            case PUSH -> pushEventMapper.mapFromWebhook(payload);
            case PULL_REQUEST -> pullRequestEventMapper.mapFromWebhook(payload);
            case ISSUES ->  issueEventMapper.mapFromWebhook(payload);
            case STAR -> starEventMapper.mapFromWebhook(payload);
            case REPOSITORY -> repositoryEventMapper.mapFromWebhook(payload);
            default -> {
                yield null;
            }
        };
    }

}
