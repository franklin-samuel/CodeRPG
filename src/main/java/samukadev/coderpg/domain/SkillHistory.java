package samukadev.coderpg.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import samukadev.coderpg.domain.enums.SkillType;

import java.time.LocalDate;
import java.util.UUID;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SkillHistory extends AbstractDomain{

    private UUID userId;

    private SkillType skillType;
    private String skillName;

    private Integer level;
    private Integer xp;

    private boolean isEquipped;

    private LocalDate firstEquippedAt;
    private LocalDate lastEquippedAt;
    private LocalDate unequippedAt;

}
