package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.IssueEventProcessorPort;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.IssueEvent;
import samukadev.coderpg.infrastructure.github.mappers.LanguageMapper;
import samukadev.coderpg.infrastructure.github.utils.ResolveSkillType;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class IssueEventProcessor implements IssueEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort processXpEventPort;
    private final UserBuildRepositoryPort userBuildRepository;

    private static final int XP_ISSUE_OPENED = 50;
    private static final int XP_ISSUE_CLOSED = 75;

    @Override
    public Boolean execute(final Context context) {
        IssueEvent event = context.getData(IssueEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(IssueEvent event) {

         Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        if (!shouldProcessAction(event.getAction())) {
            return false;
        }

        String repoLanguage = event.getRepositoryLanguage();
        String skillName = LanguageMapper.mapToSkillName(repoLanguage);

        if (skillName == null) {
            return false;
        }

        ResolveSkillType.SkillTypeResolution resolution = ResolveSkillType.execute(user.getId(), skillName, userBuildRepository);

        XpSource xpSource = determineXpSource(event.getAction());
        int xpAmount = calculateXpAmount(event.getAction());

        XpEvent xpEvent = XpEvent.builder()
                .userId(user.getId())
                .xpSource(xpSource)
                .sourceDetail("Issue #" + event.getIssueNumber() + " - " + event.getAction())
                .xpAmount(xpAmount)
                .skillType(resolution.hasSkill() ? resolution.skillType() : null)
                .skillName(resolution.hasSkill() ? resolution.skillName() : null)
                .githubEventId(event.getEventId())
                .githubRepo(event.getRepositoryFullName())
                .githubUrl("https://github.com/" + event.getRepositoryFullName() + "/issues/" + event.getIssueNumber())
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

    private XpSource determineXpSource(String action) {
        return "opened".equals(action) ? XpSource.ISSUE_OPENED : XpSource.ISSUE_CLOSED;
    }

    private int calculateXpAmount(String action) {
        return "opened".equals(action) ? XP_ISSUE_OPENED : XP_ISSUE_CLOSED;
    }

}
