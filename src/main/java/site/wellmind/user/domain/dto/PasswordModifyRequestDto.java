package site.wellmind.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PasswordModifyRequestDto
 * <p>비밀번호 변경을 위한 DTO</p>
 * @since 2024-11-28
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordModifyRequestDto {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
