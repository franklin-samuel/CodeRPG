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
public class MissionListResponse {

    private List<MissionResponse> missions;
    private Integer totalMissions;
    private Integer completedMissions;
    private Integer activeMissions;
    private Integer totalXpEarned;
    private Integer potentialXp;

}
