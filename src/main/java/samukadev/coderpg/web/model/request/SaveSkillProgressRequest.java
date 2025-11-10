package samukadev.coderpg.web.model.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samukadev.coderpg.domain.enums.SkillType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveSkillProgressRequest {

    @NotNull(message = "Skill type is required")
    private SkillType skillType;

    @NotBlank(message = "Skill name is required")
    private String skillName;

    @NotNull(message = "XP amount is required")
    @Min(value = 1, message = "XP must be greater than 0")
    private Integer xpToAdd;

}
