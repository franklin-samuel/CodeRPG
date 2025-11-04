package samukadev.coderpg.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
public class AbstractDomain {

    private UUID id;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;

    public void validate() {

    }


}
