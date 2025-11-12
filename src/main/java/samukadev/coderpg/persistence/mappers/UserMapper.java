package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.User;
import samukadev.coderpg.persistence.model.UserEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true),
        uses = {UserFollowMapper.class}
)
public interface UserMapper {

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    @Mapping(target = "active", source = "active")
    @Mapping(target = "following", source = "following")
    @Mapping(target = "followers", source = "followers")
    User map(UserEntity source);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "modifiedAt", source = "modifiedAt")
    @Mapping(target = "active", source = "active")
    UserEntity map(User source);

}
