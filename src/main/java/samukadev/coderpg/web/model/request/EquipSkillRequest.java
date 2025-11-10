package samukadev.coderpg.web.model.request;

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
public class EquipSkillRequest {

    @NotNull(message = "Skill type is required")
    private SkillType skillType;

    @NotBlank(message = "Skill name is required")
    private String skillName;

}
