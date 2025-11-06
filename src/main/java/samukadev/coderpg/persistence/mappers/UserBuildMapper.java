package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.UserBuild;
import samukadev.coderpg.persistence.model.UserBuildEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface UserBuildMapper {

    @Mapping(target = "userId", source = "user.id")
    UserBuild map(UserBuildEntity source);

    @Mapping(target = "user", ignore = true)
    UserBuildEntity map(UserBuild source);

}
