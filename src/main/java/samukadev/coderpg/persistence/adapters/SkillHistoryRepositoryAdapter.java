package samukadev.coderpg.persistence.adapters;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.persistence.SkillHistoryRepositoryPort;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.persistence.mappers.SkillHistoryMapper;
import samukadev.coderpg.persistence.repository.SkillHistoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.util.Optional.of;

@Repository
@RequiredArgsConstructor
@Transactional
public class SkillHistoryRepositoryAdapter implements SkillHistoryRepositoryPort {

    private final SkillHistoryRepository repository;
    private final SkillHistoryMapper mapper;

    @Override
    public Optional<SkillHistory> get(UUID id) {
        return repository.findById(id)
                .map(mapper::map);
    }

    @Override
    public SkillHistory save(SkillHistory model) {
        return of(repository.save(mapper.map(model)))
                .map(mapper::map)
                .orElseThrow(() -> new IllegalStateException("Failed to save skill history"));
    }

    @Override
    public List<SkillHistory> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<SkillHistory> findByUserId(UUID userId) {
        return repository.findByUserId(userId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public List<SkillHistory> findByUserIdAndIsEquipped(UUID userId, boolean isEquipped) {
        return repository.findByUserIdAndIsEquipped(userId, isEquipped).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public Optional<SkillHistory> findByUserIdAndSkillTypeAndSkillName(UUID userId, SkillType skillType, String skillName) {
        return repository.findByUserIdAndSkillTypeAndSkillName(userId, skillType, skillName)
                .map(mapper::map);
    }

    @Override
    public List<SkillHistory> findByUserIdAndSkillType(UUID userId, SkillType skillType) {
        return repository.findByUserIdAndSkillType(userId, skillType).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public boolean existsByUserIdAndSkillTypeAndSkillName(UUID userId, SkillType skillType, String skillName) {
        return repository.existsByUserIdAndSkillTypeAndSkillName(userId, skillType, skillName);
    }

    @Override
    public List<SkillHistory> findAllSkillsByUserId(UUID userId) {
        return repository.findAllSkillsByUserId(userId).stream()
                .map(mapper::map)
                .toList();
    }

    @Override
    public long countDistinctSkillsByUserId(UUID userId) {
        return repository.countDistinctSkillsByUserId(userId);
    }

    @Override
    @Transactional
    public void unequipAllByUserIdAndSkillType(UUID userId, SkillType skillType) {
        repository.unequipAllByUserIdAndSkillType(userId, skillType);
    }
}