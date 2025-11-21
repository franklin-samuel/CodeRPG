package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.PushEventProcessorPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.PushEvent;
import samukadev.coderpg.infrastructure.github.mappers.LanguageMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PushEventProcessor implements PushEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort processXpEventPort;

    private static final int BASE_XP_PER_COMMIT = 50;

    @Override
    public Boolean execute(final Context context) {
        PushEvent event = context.getData(PushEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(PushEvent event) {

        Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        String repoLanguage = event.getRepositoryLanguage();
        String skillName = LanguageMapper.mapToSkillName(repoLanguage);

        if (skillName == null) {
            return false;
        }

        int totalCommits = event.getCommitsCount() != null ? event.getCommitsCount() : event.getCommits().size();

        for (int i = 0; i < totalCommits; i++) {
            String commitSha = event.getCommits().isEmpty() ?
                    event.getAfter() + "-" + i :
                    event.getCommits().get(i).getSha();

            XpEvent xpEvent = XpEvent.builder()
                    .userId(user.getId())
                    .xpSource(XpSource.COMMIT)
                    .sourceDetail("Push to " + event.getRef())
                    .xpAmount(BASE_XP_PER_COMMIT)
                    .skillType(SkillType.PRIMARY_LANGUAGE)
                    .skillName(skillName)
                    .githubEventId(commitSha)
                    .githubUrl(buildCommitUrl(event.getRepositoryFullName(), commitSha))
                    .createdAt(event.getOccurredAt())
                    .active(true)
                    .build();

            Context context = new Context(xpEvent);
            xpEvent = calculateXpMultiplierPort.execute(context);

            context.setData(xpEvent);
            processXpEventPort.execute(context);
        }

        return true;
    }

    private String buildCommitUrl(String repoFullName, String sha) {
        return "https://github.com/" + repoFullName + "/commit/" + sha;
    }

}
