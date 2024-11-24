package site.wellmind.user.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UserDetailDto
 * <p>User Detail Data Transfer Object</p>
 * @since 2024-11-24
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private String employeeId;
    private String regNumberFor;
    private String regNumberLat;
    private boolean deleteFlag;

    //UserInfoModel
    private UserInfoDto userInfo;

    //UserEducationModel
    private List<EducationDto> education;
}
