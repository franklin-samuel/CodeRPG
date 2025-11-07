package samukadev.coderpg.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.CreateUserPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.enums.SyncStatus;
import samukadev.coderpg.domain.exceptions.BusinessException;
import samukadev.coderpg.persistence.repository.UserRepository;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class CreateUserAdapter implements CreateUserPort {

    private final UserRepositoryPort repository;

    @Override
    public User execute(final Context context) {

        User user = context.getData(User.class);

        if (user == null) {
            throw new BusinessException("User data is required");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new BusinessException("Email is required");
        }

        if (user.getGithubId() == null) {
            throw new BusinessException("Github ID is required");
        }

        if (repository.existsByEmail(user.getEmail())) {
            throw new BusinessException("Email already exists");
        }

        User newUser = User.builder()
                .githubId(user.getGithubId())
                .githubUsername(user.getGithubUsername())
                .name(user.getName())
                .email(null) 
                .avatarUrl(null)
                .bio(null)
                .location(null)
                .website(null)
                .classType(user.getClassType() != null ? user.getClassType() : ClassType.FULLSTACK)
                .level(1)
                .xp(0)
                .totalXp(0L)
                .githubPublicRepos(user.getGithubPublicRepos() != null ? user.getGithubPublicRepos() : 0)
                .githubFollowers(user.getGithubFollowers() != null ? user.getGithubFollowers() : 0)
                .githubFollowing(user.getGithubFollowing() != null ? user.getGithubFollowing() : 0)
                .githubCreatedAt(user.getGithubCreatedAt())
                .currentStreak(0)
                .longestStreak(0)
                .lastActivityDate(null)
                .lastSyncAt(LocalDateTime.now())
                .lastRespecAt(null)
                .syncStatus(SyncStatus.COMPLETED)
                .active(true)
                .build();

        return repository.save(newUser);
    }

}
