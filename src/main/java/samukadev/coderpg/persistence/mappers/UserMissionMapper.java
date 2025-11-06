package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.UserMission;
import samukadev.coderpg.persistence.model.UserMissionEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface UserMissionMapper {

    @Mapping(target = "userId", source = "user.id")
    UserMission map(UserMissionEntity source);

    @Mapping(target = "user", ignore = true)
    UserMissionEntity map(UserMission source);

}
