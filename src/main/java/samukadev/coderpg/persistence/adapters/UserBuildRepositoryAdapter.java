package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.persistence.mappers.UserBuildMapper;
import samukadev.coderpg.persistence.model.UserBuildEntity;
import samukadev.coderpg.persistence.model.UserEntity;
import samukadev.coderpg.persistence.repository.UserBuildRepository;
import samukadev.coderpg.persistence.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
@Transactional
public class UserBuildRepositoryAdapter implements UserBuildRepositoryPort {

    private final UserBuildRepository repository;
    private final UserBuildMapper mapper;
    private final UserRepository userRepository;


    @Override
    public Optional<UserBuild> get(UUID id) {
         return repository.findById(id)
                 .map(mapper::map);
    }

    @Override
    public UserBuild save(UserBuild model) {
        UserBuildEntity entity = mapper.map(model);

        if (model.getUserId() != null) {
            UserEntity user = userRepository.findById(model.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            entity.setUser(user);
        }

        UserBuildEntity entitySaved = repository.save(entity);
        return  mapper.map(entitySaved);
    }

    @Override
    public List<UserBuild> findAll() {
        return of(repository.findAll())
                .orElse(new ArrayList<>())
                .stream()
                .map(mapper::map).toList();
    }

    @Override
    public Optional<UserBuild> findByUserId(UUID userId) {
        return repository.findByUserId(userId)
                .map(mapper::map);
    }

    @Override
    @Transactional
    public void deleteByUserId(UUID userId) {
        repository.deleteByUserId(userId);
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return repository.existsByUserId(userId);
    }

}
