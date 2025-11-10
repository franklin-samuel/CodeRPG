package samukadev.coderpg.web.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillHistoryResponse {

    private List<SkillResponse> skills;
    private Integer totalSkills;
    private Integer equippedSkills;
    private List<SkillResponse> equippedSkillsList;

}