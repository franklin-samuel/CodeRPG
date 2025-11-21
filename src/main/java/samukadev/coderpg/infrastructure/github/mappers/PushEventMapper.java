package samukadev.coderpg.infrastructure.github.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.github.event.PushEvent;
import samukadev.coderpg.domain.github.model.GitHubCommit;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PushEventMapper {

    @SuppressWarnings("unchecked")
    public PushEvent mapFromWebhook(Map<String, Object> payload) {
        try {
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");
            List<Map<String, Object>> commits = (List<Map<String, Object>>) payload.get("commits");

            String ref = (String) payload.get("ref");
            String before = (String) payload.get("before");
            String after = (String) payload.get("after");
            Boolean isForce = (Boolean) payload.getOrDefault("forced", false);

            String repositoryLanguage = null;
            if (repository != null) {
                repositoryLanguage = (String) repository.get("language");
            }

            List<GitHubCommit> commitList = new ArrayList<>();
            if (commits != null) {
                for (Map<String, Object> commitData : commits) {
                    GitHubCommit commit = mapCommit(commitData);
                    if (commit != null) {
                        commitList.add(commit);
                    }
                }
            }

            return PushEvent.builder()
                    .eventId((String) payload.get("after"))
                    .eventType(GitHubEventType.PUSH)
                    .repositoryFullName((String) repository.get("full_name"))
                    .senderUsername((String) sender.get("login"))
                    .senderGithubId(((Number) sender.get("id")).longValue())
                    .occurredAt(LocalDateTime.now())
                    .ref(ref)
                    .before(before)
                    .after(after)
                    .commits(commitList)
                    .commitsCount(commitList.size())
                    .isForce(isForce)
                    .repositoryLanguage(repositoryLanguage)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private GitHubCommit mapCommit(Map<String, Object> commitData) {
        try {
            Map<String, Object> author = (Map<String, Object>) commitData.get("author");
            Map<String, Object> committer = (Map<String, Object>) commitData.get("committer");

            return GitHubCommit.builder()
                    .sha((String) commitData.get("id"))
                    .message((String) commitData.get("message"))
                    .authorName(author != null ? (String) author.get("name") : null)
                    .authorEmail(author != null ? (String) author.get("email") : null)
                    .authorDate(parseTimestamp((String) commitData.get("timestamp")))
                    .committerName(committer != null ? (String) committer.get("name") : null)
                    .committerEmail(committer != null ? (String) committer.get("email") : null)
                    .committerDate(parseTimestamp((String) commitData.get("timestamp")))
                    .url((String) commitData.get("url"))
                    .build();
        } catch (Exception e) {
            return null;
        }
    }

    private LocalDateTime parseTimestamp(String timestamp) {
        if (timestamp == null) return LocalDateTime.now();
        try {
            return ZonedDateTime.parse(timestamp).toLocalDateTime();
        } catch (Exception e) {
            return LocalDateTime.now();
        }
    }
}
