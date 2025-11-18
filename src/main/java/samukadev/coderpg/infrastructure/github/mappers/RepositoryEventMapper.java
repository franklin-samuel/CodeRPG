package samukadev.coderpg.infrastructure.github.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.github.event.RepositoryEvent;
import samukadev.coderpg.domain.github.model.GitHubRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class RepositoryEventMapper {

    @SuppressWarnings("unchecked")
    public RepositoryEvent mapFromWebhook(Map<String, Object> payload) {
        try {
            Map<String, Object> repoData = (Map<String, Object>) payload.get("repository");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");

            String action = (String) payload.get("action");

            GitHubRepository repository = mapRepository(repoData);

            return RepositoryEvent.builder()
                    .eventId(String.valueOf(repoData.get("id")))
                    .eventType(GitHubEventType.REPOSITORY)
                    .repositoryFullName((String) repoData.get("full_name"))
                    .senderUsername((String) sender.get("login"))
                    .senderGithubId(((Number) sender.get("id")).longValue())
                    .occurredAt(LocalDateTime.now())
                    .action(action)
                    .repository(repository)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }

    private GitHubRepository mapRepository(Map<String, Object> repoData) {
        return GitHubRepository.builder()
                .id(((Number) repoData.get("id")).longValue())
                .name((String) repoData.get("name"))
                .fullName((String) repoData.get("full_name"))
                .description((String) repoData.get("description"))
                .isPrivate((Boolean) repoData.getOrDefault("private", false))
                .isFork((Boolean) repoData.getOrDefault("fork", false))
                .language((String) repoData.get("language"))
                .stargazersCount((Integer) repoData.getOrDefault("stargazers_count", 0))
                .forksCount((Integer) repoData.getOrDefault("forks_count", 0))
                .openIssuesCount((Integer) repoData.getOrDefault("open_issues_count", 0))
                .createdAt(parseTimestamp((String) repoData.get("created_at")))
                .updatedAt(parseTimestamp((String) repoData.get("updated_at")))
                .pushedAt(parseTimestamp((String) repoData.get("pushed_at")))
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
