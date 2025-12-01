package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.PushEventProcessorPort;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.PushEvent;
import samukadev.coderpg.infrastructure.github.mappers.LanguageMapper;
import samukadev.coderpg.infrastructure.github.utils.ResolveSkillType;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PushEventProcessor implements PushEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort processXpEventPort;
    private final UserBuildRepositoryPort userBuildRepository;

    private static final int BASE_XP_PER_COMMIT = 50;

    @Override
    public Boolean execute(final Context context) {
        PushEvent event = context.getData(PushEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(PushEvent event) {
        try {
            log.info("Processing push event for repository: {}", event.getRepositoryFullName());

            if (event == null) {
                log.error("Push event is null");
                return false;
            }

            event.validate();

            Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
            if (userOpt.isEmpty()) {
                log.warn("User not found for GitHub ID: {}", event.getSenderGithubId());
                return false;
            }

            User user = userOpt.get();
            log.debug("Processing push for user: {} ({})", user.getGithubUsername(), user.getId());

            String repoLanguage = event.getRepositoryLanguage();
            if (repoLanguage == null || repoLanguage.isBlank()) {
                log.warn("Repository language is null or empty for repo: {}", event.getRepositoryFullName());
                return false;
            }

            String skillName = LanguageMapper.mapToSkillName(repoLanguage);
            if (skillName == null) {
                log.warn("Language '{}' is not mapped to any skill", repoLanguage);
                return false;
            }

            log.debug("Mapped language '{}' to skill '{}'", repoLanguage, skillName);

            ResolveSkillType.SkillTypeResolution resolution =
                    ResolveSkillType.execute(user.getId(), skillName, userBuildRepository);

            int totalCommits = calculateTotalCommits(event);
            log.info("Processing {} commits for user {}", totalCommits, user.getGithubUsername());

            if (totalCommits == 0) {
                log.warn("No commits to process in push event");
                return false;
            }

            for (int i = 0; i < totalCommits; i++) {
                String commitSha = extractCommitSha(event, i);
                processCommit(user, event, commitSha, resolution, i);
            }

            return true;

        } catch (Exception e) {
            log.error("Error processing push event: {}", e.getMessage(), e);
            return false;
        }
    }

    private int calculateTotalCommits(PushEvent event) {
        if (event.getCommitsCount() != null && event.getCommitsCount() > 0) {
            return event.getCommitsCount();
        }

        if (event.getCommits() != null) {
            return event.getCommits().size();
        }

        log.warn("No commit count or commit list available in push event");
        return 0;
    }

    private String extractCommitSha(PushEvent event, int index) {
        try {
            if (event.getCommits() != null && !event.getCommits().isEmpty() && index < event.getCommits().size()) {
                return event.getCommits().get(index).getSha();
            }

            return event.getAfter() + "-" + index;

        } catch (Exception e) {
            log.warn("Error extracting commit SHA at index {}, using fallback", index);
            return event.getAfter() + "-" + index;
        }
    }

    private void processCommit(
            User user,
            PushEvent event,
            String commitSha,
            ResolveSkillType.SkillTypeResolution resolution,
            int commitIndex
    ) {
        try {
            XpEvent xpEvent = XpEvent.builder()
                    .userId(user.getId())
                    .xpSource(XpSource.COMMIT)
                    .sourceDetail("Push to " + event.getRef())
                    .xpAmount(BASE_XP_PER_COMMIT)
                    .skillType(resolution.hasSkill() ? resolution.skillType() : null)
                    .skillName(resolution.hasSkill() ? resolution.skillName() : null)
                    .githubEventId(commitSha)
                    .githubRepo(event.getRepositoryFullName())
                    .githubUrl(buildCommitUrl(event.getRepositoryFullName(), commitSha))
                    .createdAt(event.getOccurredAt())
                    .active(true)
                    .build();

            Context context = new Context(xpEvent);
            xpEvent = calculateXpMultiplierPort.execute(context);

            context.setData(xpEvent);
            processXpEventPort.execute(context);

            log.debug("Processed commit {}/{} for user {}",
                    commitIndex + 1,
                    calculateTotalCommits(event),
                    user.getGithubUsername()
            );

        } catch (Exception e) {
            log.error("Error processing commit {} for user {}: {}",
                    commitSha, user.getGithubUsername(), e.getMessage(), e);
        }
    }

    private String buildCommitUrl(String repoFullName, String sha) {
        String cleanSha = sha.contains("-") ? sha.substring(0, sha.lastIndexOf("-")) : sha;
        return "https://github.com/" + repoFullName + "/commit/" + cleanSha;
    }
}