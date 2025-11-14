package samukadev.coderpg.web.model.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import samukadev.coderpg.domain.enums.ClassType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteOnBoardingRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "ClassType is required")
    private ClassType classType;

}
