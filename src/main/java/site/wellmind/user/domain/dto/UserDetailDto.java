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
    //UserTopModel
    private String employeeId;
    private String regNumberFor;
    private String regNumberLat;
    //UserInfoModel
    private UserInfoDto userInfo;
    //UserSignificantModel
    private UserSignificantDto userSignificant;
    //UserEducationModel
    private List<EducationDto> education;
}
