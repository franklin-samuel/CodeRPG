package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.Activity;
import samukadev.coderpg.persistence.model.ActivityEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface ActivityMapper {

    @Mapping(target = "userId", source = "user.id")
    Activity map(ActivityEntity source);

    @Mapping(target = "user", ignore = true)
    ActivityEntity map(Activity source);

}
