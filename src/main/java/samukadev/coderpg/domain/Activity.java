package samukadev.coderpg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.ActivityType;

import java.util.Map;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Activity extends AbstractDomain{

    private UUID userId;
    private ActivityType type;
    private Map<String, Object> data;
    private boolean isPublic;
    private Integer likesCount;
    private Integer commentsCount;

}
