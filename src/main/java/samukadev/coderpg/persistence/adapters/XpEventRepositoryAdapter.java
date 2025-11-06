package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import samukadev.coderpg.core.persistence.XpEventRepositoryPort;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;
import samukadev.coderpg.persistence.mappers.XpEventMapper;
import samukadev.coderpg.persistence.repository.XpEventRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class XpEventRepositoryAdapter implements XpEventRepositoryPort {

    private final XpEventRepository repository;

    private final XpEventMapper mapper;

    @Override
    public Optional<XpEvent> get(UUID id) {
        return repository.findById(id)
                .map(mapper::map);
    }

    @Override
    public XpEvent save(XpEvent model) {
        return of(repository.save(mapper.map(model)))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to save xp event"));
    }

    @Override
    public List<XpEvent> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<XpEvent> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<XpEvent> findByUserIdAndSkillType(UUID userId, SkillType skillType) {
        return repository.findByUserIdAndSkillType(userId, skillType).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<XpEvent> findByUserIdAndXpSource(UUID userId, XpSource xpSource) {
        return repository.findByUserIdAndXpSource(userId, xpSource).stream()
                .map(mapper::map).toList();
    }

    @Override
    public List<XpEvent> findByUserIdAndCreatedAtBetween(
            UUID userId,
            LocalDateTime start,
            LocalDateTime end
    ) {
        return repository.findByUserIdAndCreatedAtBetween(userId, start, end).stream()
                .map(mapper::map).toList();
    }

    @Override
    public Optional<XpEvent> findByGithubEventId(String githubEventId) {
        return repository.findByGithubEventId(githubEventId)
                .map(mapper::map);
    }

    @Override
    public boolean existsByGithubEventId(String githubEventId) {
        return repository.existsByGithubEventId(githubEventId);
    }

}
