package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.SkillHistory;
import samukadev.coderpg.persistence.model.SkillHistoryEntity;

@Mapper(
      componentModel = "spring",
      builder = @Builder(disableBuilder = true)
)
public interface SkillHistoryMapper {

    @Mapping(target = "userId", source = "user.id")
    SkillHistory map(SkillHistoryEntity source);

    @Mapping(target = "user", ignore = true)
    SkillHistoryEntity map(SkillHistory source);

}
