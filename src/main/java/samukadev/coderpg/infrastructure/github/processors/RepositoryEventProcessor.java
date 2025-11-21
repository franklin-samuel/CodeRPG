package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.RepositoryEventProcessorPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.RepositoryEvent;
import samukadev.coderpg.infrastructure.github.mappers.LanguageMapper;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RepositoryEventProcessor implements RepositoryEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort  processXpEventPort;

    private static final int XP_REPO_CREATED = 100;

    @Override
    public Boolean execute(final Context context) {
        RepositoryEvent event = context.getData(RepositoryEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(RepositoryEvent event) {

        if (!"created".equals(event.getAction())) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
        if (userOpt.isEmpty()) return false;

        User user = userOpt.get();

        String repoLanguage = event.getRepositoryLanguage();
        String skillName = LanguageMapper.mapToSkillName(repoLanguage);

        if (skillName == null) {
            return false;
        }

        XpEvent xpEvent =  XpEvent.builder()
                .userId(user.getId())
                .xpSource(XpSource.REPO_CREATED)
                .sourceDetail("Created Repository: " + event.getRepositoryFullName())
                .xpAmount(XP_REPO_CREATED)
                .skillType(null)
                .skillName(null)
                .githubEventId(event.getEventId())
                .githubRepo(event.getRepositoryFullName())
                .githubUrl("https://github.com/" + event.getRepositoryFullName())
                .createdAt(event.getOccurredAt())
                .active(true)
                .build();

        Context context = new Context(xpEvent);
        xpEvent = calculateXpMultiplierPort.execute(context);

        context.setData(xpEvent);
        processXpEventPort.execute(context);

        return true;

    }

}
