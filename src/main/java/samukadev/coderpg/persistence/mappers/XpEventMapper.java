package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.XpEvent;
import samukadev.coderpg.persistence.model.XpEventEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface XpEventMapper {

    @Mapping(target = "userId", source = "user.id")
    XpEvent map(XpEventEntity source);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active", ignore = true)
    XpEventEntity map(XpEvent source);

}
