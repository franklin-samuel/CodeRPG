package samukadev.coderpg.infrastructure.github.processors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.xp.CalculateXpMultiplierPort;
import samukadev.coderpg.core.business.xp.ProcessXpEventPort;
import samukadev.coderpg.core.integration.github.event.StarEventProcessorPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.domain.github.event.StarEvent;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class StarEventProcessor implements StarEventProcessorPort {

    private final UserRepositoryPort userRepository;
    private final CalculateXpMultiplierPort calculateXpMultiplierPort;
    private final ProcessXpEventPort processXpEventPort;

    private static final int XP_STAR_RECEIVED = 25;

    @Override
    public Boolean execute(Context context) {
        StarEvent event = context.getData(StarEvent.class);
        return processEvent(event);
    }

    @Override
    public Boolean processEvent(StarEvent event) {

        if (!"created".equals(event.getAction())) {
            return false;
        }

        Optional<User> userOpt = userRepository.findByGitHubId(event.getSenderGithubId());
        if (userOpt.isEmpty()) return false;

        User  user = userOpt.get();

        XpEvent xpEvent = XpEvent.builder()
                .userId(user.getId())
                .xpSource(XpSource.STAR_RECEIVED)
                .sourceDetail("Received star on " + event.getRepositoryFullName())
                .xpAmount(XP_STAR_RECEIVED)
                .skillType(null)
                .skillName(null)
                .githubEventId(event.getEventId())
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
