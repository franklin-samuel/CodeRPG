package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.PullRequestEventProcessorPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.PullRequestEvent;
import samukadev.coderpg.infrastructure.github.mappers.LanguageMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PullRequestEventProcessor implements PullRequestEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort processXpEventPort;

    private static final int XP_PR_OPENED = 75;
    private static final int XP_PR_MERGED = 150;

    @Override
    public Boolean execute(final Context context) {
        PullRequestEvent event = context.getData(PullRequestEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(PullRequestEvent event) {

        Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        if (!shouldProcessAction(event.getAction())) {
            return false;
        }

        String repoLanguage = event.getRepositoryLanguage();
        String skillName = LanguageMapper.mapToSkillName(repoLanguage);

        if (skillName == null) return false;

        XpSource xpSource = determineXpSource(event.getAction(), event.getWasMerged());
        int xpAmount = calculateXpAmount(event.getAction(), event.getWasMerged());

        XpEvent xpEvent = XpEvent.builder()
                .userId(user.getId())
                .xpSource(xpSource)
                .sourceDetail("PR #" + event.getPullRequest().getNumber() + " - " + event.getAction())
                .xpAmount(xpAmount)
                .skillType(SkillType.PRIMARY_LANGUAGE)
                .skillName(skillName)
                .githubEventId(event.getEventId())
                .githubRepo(event.getRepositoryFullName())
                .githubUrl("https://github.com/" + event.getRepositoryFullName() + "/pull/" + event.getPullRequest().getNumber())
                .createdAt(event.getOccurredAt())
                .active(true)
                .build();

        Context context = new Context(xpEvent);
        xpEvent = calculateXpMultiplierPort.execute(context);

        context.setData(xpEvent);
        processXpEventPort.execute(context);

        return true;
        
    }

    private boolean shouldProcessAction(String action) {
        return "opened".equals(action) || "closed".equals(action);
    }

    private XpSource determineXpSource(String action, Boolean wasMerged) {
        if ("opened".equals(action)) {
            return XpSource.PR_OPENED;
        }
        if ("closed".equals(action) && Boolean.TRUE.equals(wasMerged)) {
            return XpSource.PR_MERGED;
        }
        return XpSource.PR_OPENED;
    }

    private int calculateXpAmount(String action, Boolean wasMerged) {
        if ("opened".equals(action)) {
            return XP_PR_OPENED;
        }
        if ("closed".equals(action) && Boolean.TRUE.equals(wasMerged)) {
            return XP_PR_MERGED;
        }
        return 0;
    }

}
