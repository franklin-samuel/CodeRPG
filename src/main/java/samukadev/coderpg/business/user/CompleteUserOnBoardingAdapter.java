package samukadev.coderpg.business.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import samukadev.coderpg.core.Context;
import samukadev.coderpg.core.business.user.CompleteUserOnBoardingPort;
import samukadev.coderpg.core.persistence.UserRepositoryPort;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.domain.enums.ClassType;
import samukadev.coderpg.domain.exceptions.BusinessException;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CompleteUserOnBoardingAdapter implements CompleteUserOnBoardingPort {

    private final UserRepositoryPort repository;

    @Override
    public User execute(final Context context) {

        UUID userId = context.getProperty("userId", UUID.class);
        String name = context.getProperty("name", String.class);
        ClassType classType = context.getProperty("classType", ClassType.class);

        if (userId == null) {
            throw new BusinessException("User ID is required");
        }

        if (name == null || name.isBlank()) {
            throw new BusinessException("Name is required");
        }

        if (classType == null) {
            throw new BusinessException("Class type is required");
        }

        User user = repository.get(userId)
                .orElseThrow(() -> new BusinessException("User not found"));

        user.setName(name);
        user.setClassType(classType);

        User updatedUser = repository.save(user);

        context.putProperty("onBoardingComplete", true);

        return updatedUser;

    }

}
