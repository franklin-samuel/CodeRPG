package samukadev.coderpg.web.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("redirectUri")
    private String redirectUri;

}
