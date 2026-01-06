package samukadev.coderpg.web.model.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MobileAuthRequest {

    @NotBlank
    private String code;

    @NotBlank
    private String redirectUri;

}
