package samukadev.coderpg.domain;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.SkillType;
import samukadev.coderpg.domain.enums.XpSource;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class XpEvent extends AbstractDomain{

    private UUID userId;

    private XpSource xpSource;
    private String sourceDetail;
    private Integer xpAmount;
    private SkillType skillType;
    private String skillName;

    private String githubEventId;
    private String githubRepo;
    private String githubUrl;

    private BigDecimal multiplier;
    private List<String> multiplierReasons;

}
