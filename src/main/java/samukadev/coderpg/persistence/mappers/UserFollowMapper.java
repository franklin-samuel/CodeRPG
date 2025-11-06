package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.UserFollow;
import samukadev.coderpg.persistence.model.UserFollowEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface UserFollowMapper {

    @Mapping(target = "followerId", source = "follower.id")
    @Mapping(target = "followingId", source = "following.id")
    UserFollow map(UserFollowEntity source);

    @Mapping(target = "follower", ignore = true)
    @Mapping(target = "following", ignore = true)
    @Mapping(target = "active", ignore = true)
    UserFollowEntity map(UserFollow source);

}
