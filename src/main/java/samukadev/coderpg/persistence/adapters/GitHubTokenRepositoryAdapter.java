package samukadev.coderpg.persistence.adapters;

import static java.util.Optional.of;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.persistence.GitHubTokenRepositoryPort;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.persistence.mappers.GitHubTokenMapper;
import samukadev.coderpg.persistence.repository.GitHubTokenRepository;

import java.util.Optional;
import java.util.UUID;


@Repository
@RequiredArgsConstructor
@Transactional
public class GitHubTokenRepositoryAdapter implements GitHubTokenRepositoryPort {

    private final GitHubTokenRepository gitHubTokenRepository;
    private final GitHubTokenMapper mapper;

    @Override
    public GitHubToken save(GitHubToken token) {
        return of(gitHubTokenRepository.save(mapper.map(token)))
                .map(mapper::map)
                .orElseThrow(() -> new RuntimeException("Failed to save user github token"));
    }

    @Override
    public Optional<GitHubToken> findFirstByUserIdOrderByCreatedAtDesc(UUID userId) {
        return gitHubTokenRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .map(mapper::map);
    }

    @Override
    public Optional<GitHubToken> findByUserIdAndActiveTrue(final UUID userId) {
        return gitHubTokenRepository.findByUserIdAndActiveTrue(userId)
                .map(mapper::map);
    }

    @Override
    public void deactiveAllByUserId(final UUID userId) {
        gitHubTokenRepository.deactiveAllByUserId(userId);
    }

}


