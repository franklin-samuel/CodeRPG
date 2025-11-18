package samukadev.coderpg.infrastructure.github.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.github.event.PullRequestEvent;
import samukadev.coderpg.domain.github.model.GitHubPullRequest;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PullRequestEventMapper {

    @SuppressWarnings("unchecked")
    public PullRequestEvent mapFromWebhook(Map<String, Object> payload) {
        try {
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");
            Map<String, Object> prData = (Map<String, Object>) payload.get("pull_request");

            String action = (String) payload.get("action");
            Boolean wasMerged = (Boolean) prData.getOrDefault("merged", false);

            GitHubPullRequest pullRequest = mapPullRequest(prData);

            return PullRequestEvent.builder()
                    .eventId(String.valueOf(prData.get("id")))
                    .eventType(GitHubEventType.PULL_REQUEST)
                    .repositoryFullName((String) repository.get("full_name"))
                    .senderUsername((String) sender.get("login"))
                    .senderGithubId(((Number) sender.get("id")).longValue())
                    .occurredAt(LocalDateTime.now())
                    .action(action)
                    .pullRequest(pullRequest)
                    .wasMerged(wasMerged)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private GitHubPullRequest mapPullRequest(Map<String, Object> prData) {
        Map<String, Object> base = (Map<String, Object>) prData.get("base");
        Map<String, Object> head = (Map<String, Object>) prData.get("head");
        Map<String, Object> user = (Map<String, Object>) prData.get("user");

        return GitHubPullRequest.builder()
                .id(((Number) prData.get("id")).longValue())
                .number((Integer) prData.get("number"))
                .title((String) prData.get("title"))
                .body((String) prData.get("body"))
                .state((String) prData.get("state"))
                .merged((Boolean) prData.getOrDefault("merged", false))
                .baseBranch(base != null ? (String) base.get("ref") : null)
                .headBranch(head != null ? (String) head.get("ref") : null)
                .userLogin(user != null ? (String) user.get("login") : null)
                .additions((Integer) prData.getOrDefault("additions", 0))
                .deletions((Integer) prData.getOrDefault("deletions", 0))
                .changedFiles((Integer) prData.getOrDefault("changed_files", 0))
                .createdAt(parseTimestamp((String) prData.get("created_at")))
                .updatedAt(parseTimestamp((String) prData.get("updated_at")))
                .mergedAt(parseTimestamp((String) prData.get("merged_at")))
                .closedAt(parseTimestamp((String) prData.get("closed_at")))
                .build();
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null) return null;
        try {
            return ZonedDateTime.parse(timestamp).toLocalDateTime();
        } catch (Exception e) {
            return null;
        }
    }
}
