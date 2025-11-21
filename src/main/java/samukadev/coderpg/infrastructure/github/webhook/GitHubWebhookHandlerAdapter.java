package samukadev.coderpg.infrastructure.github.webhook;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.integration.github.GitHubWebHookHandlerPort;
import samukadev.coderpg.core.integration.github.event.*;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.exceptions.GitHubWebhookException;
import samukadev.coderpg.domain.github.GitHubEvent;
import samukadev.coderpg.domain.github.GitHubWebhookPayload;
import samukadev.coderpg.domain.github.event.*;
import samukadev.coderpg.infrastructure.github.mappers.*;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class GitHubWebhookHandlerAdapter implements GitHubWebHookHandlerPort {

    private final UserRepositoryPort userRepository;
    private final PushEventMapper pushEventMapper;
    private final PullRequestEventMapper pullRequestEventMapper;
    private final IssueEventMapper issueEventMapper;
    private final StarEventMapper starEventMapper;
    private final RepositoryEventMapper repositoryEventMapper;
    private final PushEventProcessorPort pushEventProcessor;
    private final PullRequestEventProcessorPort pullRequestEventProcessor;
    private final IssueEventProcessorPort issueEventProcessor;
    private final StarEventProcessorPort starEventProcessor;
    private final RepositoryEventProcessorPort repositoryEventProcessor;

    @Override
    public Boolean execute(Context context) {
        GitHubWebhookPayload payload = context.getData(GitHubWebhookPayload.class);
        String eventType = context.getProperty("eventType", String.class);
        return processWebhook(payload, eventType);
    }

    @Override
    @Async
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public Boolean processWebhook(GitHubWebhookPayload payload, String eventType) {
        try {
            GitHubEventType type = GitHubEventType.fromValue(eventType);

            if (type == GitHubEventType.UNKNOWN) {
                return false;
            }

            Map<String, Object> payloadMap = payload.getPayload();

            @SuppressWarnings("unchecked")
            Map<String, Object> sender = (Map<String, Object>) payloadMap.get("sender");
            if (sender == null) {
                return false;
            }

            Long senderGitHubId = ((Number) sender.get("id")).longValue();

            Optional<User> userOpt = userRepository.findByGitHubId(senderGitHubId);
            if (userOpt.isEmpty()) {
                return false;
            }

            GitHubEvent event = mapEvent(type, payloadMap);
            if (event == null) {
                return false;
            }

            event.validate();

            return processEvent(type, event);

        } catch (IllegalArgumentException e) {
            return false;
        } catch (Exception e) {
            throw new GitHubWebhookException("Webhook processing failed", e);
        }
    }

    private GitHubEvent mapEvent(GitHubEventType type, Map<String, Object> payload) {
        return switch (type) {
            case PUSH -> pushEventMapper.mapFromWebhook(payload);
            case PULL_REQUEST -> pullRequestEventMapper.mapFromWebhook(payload);
            case ISSUES -> issueEventMapper.mapFromWebhook(payload);
            case STAR -> starEventMapper.mapFromWebhook(payload);
            case REPOSITORY -> repositoryEventMapper.mapFromWebhook(payload);
            default -> null;
        };
    }

    private boolean processEvent(GitHubEventType type, GitHubEvent event) {
        return switch (type) {
            case PUSH -> {
                Context ctx = new Context(event);
                yield pushEventProcessor.processEvent((PushEvent) event);
            }
            case PULL_REQUEST -> {
                yield pullRequestEventProcessor.processEvent((PullRequestEvent) event);
            }
            case ISSUES -> {
                yield issueEventProcessor.processEvent((IssueEvent) event);
            }
            case STAR -> {
                yield starEventProcessor.processEvent((StarEvent) event);
            }
            case REPOSITORY -> {
                yield repositoryEventProcessor.processEvent((RepositoryEvent) event);
            }
            default -> false;
        };
    }
}