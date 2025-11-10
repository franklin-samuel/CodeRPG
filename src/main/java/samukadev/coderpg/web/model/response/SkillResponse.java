package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samukadev.coderpg.domain.enums.SkillType;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private UUID id;
    private UUID userId;

    private SkillType skillType;
    private String skillTypeName;
    private String skillName;

    private Integer level;
    private Integer xp;
    private Integer xpToNextLevel;

    private Boolean isEquipped;

    private LocalDateTime firstEquippedAt;
    private LocalDateTime lastEquippedAt;
    private LocalDateTime unequippedAt;

    private LocalDateTime createdAt;

}
