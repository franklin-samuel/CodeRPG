package samukadev.coderpg.infrastructure.github.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.github.event.StarEvent;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class StarEventMapper {

    @SuppressWarnings("unchecked")
    public StarEvent mapFromWebhook(Map<String, Object> payload) {
        try {
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");

            String action = (String) payload.get("action");
            Integer stargazersCount = (Integer) repository.get("stargazers_count");

            return StarEvent.builder()
                    .eventId(repository.get("id") + "-" + sender.get("id"))
                    .eventType(GitHubEventType.STAR)
                    .repositoryFullName((String) repository.get("full_name"))
                    .senderUsername((String) sender.get("login"))
                    .senderGithubId(((Number) sender.get("id")).longValue())
                    .occurredAt(LocalDateTime.now())
                    .action(action)
                    .stargazersCount(stargazersCount)
                    .build();

        } catch (Exception e) {
            return null;
        }
    }
}
