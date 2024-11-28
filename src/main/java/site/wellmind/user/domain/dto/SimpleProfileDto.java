package site.wellmind.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.vo.AddressVO;

/**
 * SimpleProfileDto
 * <p>Simple Profile Data Transfer Object</p>
 * @since 2024-11-28
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SimpleProfileDto {
    private String email;
    private String name;
    private String phoneNum;
    private String authType;
    private String employeeId;
    private boolean deleteFlag;

    private String photo;
    private AddressVO address;

    private String departName;
    private String positionName;
}
