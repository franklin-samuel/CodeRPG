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

@Slf4j
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
        processWebhook(payload, eventType);
        return null;
    }

    @Override
    @Async
    @Retryable(
            value = {Exception.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 1000, multiplier = 2)
    )
    public void processWebhook(GitHubWebhookPayload payload, String eventType) {
        try {
            GitHubEventType type = GitHubEventType.fromValue(eventType);

            if (type == GitHubEventType.UNKNOWN) {
                log.warn("Unknown event type: {}", eventType);
                return;
            }

            Map<String, Object> payloadMap = payload.getPayload();

            @SuppressWarnings("unchecked")
            Map<String, Object> sender = (Map<String, Object>) payloadMap.get("sender");
            if (sender == null) {
                log.warn("No sender in webhook payload");
                return;
            }

            Long senderGitHubId = ((Number) sender.get("id")).longValue();

            Optional<User> userOpt = userRepository.findByGitHubId(senderGitHubId);
            if (userOpt.isEmpty()) {
                log.info("User not found for GitHub ID: {}", senderGitHubId);
                return;
            }

            GitHubEvent event = mapEvent(type, payloadMap);
            if (event == null) {
                log.warn("Could not map event of type: {}", type);
                return;
            }

            event.validate();

            processEvent(type, event);

            log.info("Successfully processed {} event for user {}", type, senderGitHubId);

        } catch (IllegalArgumentException e) {
            log.error("Validation error processing webhook: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error processing webhook: {}", e.getMessage(), e);
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

    private void processEvent(GitHubEventType type, GitHubEvent event) {
        switch (type) {
            case PUSH -> pushEventProcessor.processEvent((PushEvent) event);
            case PULL_REQUEST -> pullRequestEventProcessor.processEvent((PullRequestEvent) event);
            case ISSUES -> issueEventProcessor.processEvent((IssueEvent) event);
            case STAR -> starEventProcessor.processEvent((StarEvent) event);
            case REPOSITORY -> repositoryEventProcessor.processEvent((RepositoryEvent) event);
        }
    }
}