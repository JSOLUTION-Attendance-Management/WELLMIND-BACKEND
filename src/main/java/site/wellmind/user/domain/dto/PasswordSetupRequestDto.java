package site.wellmind.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PasswordSetupRequestDto
 * <p>비밀번호 설정을 위한 DTO</p>
 * @since 2024-11-12
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordSetupRequestDto {
    private String token;
    private String newPassword;
    private String confirmNewPassword;
}
