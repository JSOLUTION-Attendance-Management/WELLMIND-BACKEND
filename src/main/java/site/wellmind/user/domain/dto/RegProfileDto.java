package site.wellmind.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.vo.AddressVO;

/**
 * RegProfileDto
 * <p>Reg Profile Data Transfer Object</p>
 * @since 2024-11-27
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RegProfileDto {
    private String email;
    private String name;
    private String phoneNum;
    private String authType;
    private String employeeId;

    private String departName;
    private String positionName;
}
