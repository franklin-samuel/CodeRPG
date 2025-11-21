package samukadev.coderpg.infrastructure.github.mappers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import samukadev.coderpg.domain.enums.GitHubEventType;
import samukadev.coderpg.domain.github.event.IssueEvent;

import java.time.LocalDateTime;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class IssueEventMapper {

    @SuppressWarnings("unchecked")
    public IssueEvent mapFromWebhook(Map<String, Object> payload) {
        try {
            Map<String, Object> repository = (Map<String, Object>) payload.get("repository");
            Map<String, Object> sender = (Map<String, Object>) payload.get("sender");
            Map<String, Object> issue = (Map<String, Object>) payload.get("issue");

            String action = (String) payload.get("action");

            String repositoryLanguage = null;
            if (repository != null) {
                repositoryLanguage = (String) repository.get("language");
            }

            return IssueEvent.builder()
                    .eventId(String.valueOf(issue.get("id")))
                    .eventType(GitHubEventType.ISSUES)
                    .repositoryFullName((String) repository.get("full_name"))
                    .repositoryLanguage(repositoryLanguage)
                    .senderUsername((String) sender.get("login"))
                    .senderGithubId(((Number) sender.get("id")).longValue())
                    .occurredAt(LocalDateTime.now())
                    .action(action)
                    .issueId(((Number) issue.get("id")).longValue())
                    .issueNumber((Integer) issue.get("number"))
                    .issueTitle((String) issue.get("title"))
                    .issueBody((String) issue.get("body"))
                    .issueState((String) issue.get("state"))
                    .build();

        } catch (Exception e) {
            return null;
        }
    }
}
