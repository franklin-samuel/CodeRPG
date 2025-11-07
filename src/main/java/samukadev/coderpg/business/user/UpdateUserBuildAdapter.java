package samukadev.coderpg.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.UpdateUserBuildPort;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class UpdateUserBuildAdapter implements UpdateUserBuildPort {

    private final UserBuildRepositoryPort buildRepository;
    private final UserRepositoryPort userRepository;

    @Override
    public User execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);
        UserBuild buildData = context.getProperty("buildData", UserBuild.class);

        if (userId == null) {
            throw new BusinessException("User ID is required");
        }

        if (buildData == null) {
            throw new BusinessException("Build Data is required");
        }

        User user = userRepository.get(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        Optional<UserBuild> existingBuild = buildRepository.findByUserId(userId);

        UserBuild userBuild;
        if (existingBuild.isPresent()) {
            userBuild = existingBuild.get();
        } else {
            userBuild = UserBuild.builder()
                    .userId(userId)
                    .primaryLanguageLevel(1)
                    .primaryLanguageXp(0)
                    .secondaryLanguageLevel(1)
                    .secondaryLanguageXp(0)
                    .active(true)
                    .build();
        }

        if (buildData.getPrimaryLanguage() != null) {
            userBuild.setPrimaryLanguage(buildData.getPrimaryLanguage());
        }
        if (buildData.getSecondaryLanguage() != null) {
            userBuild.setSecondaryLanguage(buildData.getSecondaryLanguage());
        }
        if (buildData.getFramework() != null) {
            userBuild.setFramework(buildData.getFramework());
        }
        if (buildData.getDatabase() != null) {
            userBuild.setDatabase(buildData.getDatabase());
        }
        if (buildData.getCloud() != null) {
            userBuild.setCloud(buildData.getCloud());
        }
        if (buildData.getTool1() != null) {
            userBuild.setTool1(buildData.getTool1());
        }
        if (buildData.getTool2() != null) {
            userBuild.setTool2(buildData.getTool2());
        }

        UserBuild savedBuild = buildRepository.save(userBuild);
        context.putProperty("updatedBuild", savedBuild);

        return user;

    }

}
