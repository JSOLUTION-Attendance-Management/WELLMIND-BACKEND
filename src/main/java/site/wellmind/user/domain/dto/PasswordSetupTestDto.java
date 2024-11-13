package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordSetupTestDto {
    @JsonProperty("email")
    private String email;

    @JsonProperty("employeeId")
    private String employeeId;
}
