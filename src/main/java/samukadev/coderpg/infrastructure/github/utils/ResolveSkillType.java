package samukadev.coderpg.infrastructure.github.utils;

import lombok.experimental.UtilityClass;
import samukadev.coderpg.core.persistence.UserBuildRepositoryPort;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.domain.enums.SkillType;

import java.util.Optional;
import java.util.UUID;

@UtilityClass
public class ResolveSkillType {

    public static SkillTypeResolution execute(
            UUID userId,
            String languageName,
            UserBuildRepositoryPort buildRepository
    ) {

        Optional<UserBuild> buildOpt = buildRepository.findByUserId(userId);

        if (buildOpt.isEmpty()) {
            return new SkillTypeResolution(null, null, false    );
        }

        UserBuild build = buildOpt.get();

        if (languageName.equalsIgnoreCase(build.getPrimaryLanguage())) {
            return new SkillTypeResolution(SkillType.PRIMARY_LANGUAGE, languageName, true);
        }

        if (languageName.equalsIgnoreCase(build.getSecondaryLanguage())) {
            return new SkillTypeResolution(SkillType.SECONDARY_LANGUAGE, languageName, true);
        }

        return new SkillTypeResolution(null, languageName, false);

    }

    public record SkillTypeResolution(SkillType skillType, String skillName, boolean hasSkill) {}

}
