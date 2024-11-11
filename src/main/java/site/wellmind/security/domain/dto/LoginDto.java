package site.wellmind.security.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * LoginDto
 * <p>Login Data Transfer Object</p>
 * @since 2024-11-03
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LoginDto {
    @NotNull(message = "사원 번호은 필수입니다.")
    private String employeeId;
    @NotNull(message = "패스워드 입력은 필수입니다.")
    private String password;
}
