package samukadev.coderpg.persistence.mappers;

import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import samukadev.coderpg.domain.GitHubToken;
import samukadev.coderpg.persistence.model.GitHubTokenEntity;

@Mapper(
        componentModel = "spring",
        builder = @Builder(disableBuilder = true)
)
public interface GitHubTokenMapper {

    @Mapping(target = "userId", source = "user.id")
    GitHubToken map(GitHubTokenEntity source);

    @Mapping(target = "user", ignore = true)
    GitHubTokenEntity map(GitHubToken source);

}
