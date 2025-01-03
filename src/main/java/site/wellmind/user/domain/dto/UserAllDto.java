package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * UserDTO
 * <p>User Total Data Transfer Object</p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAllDto {
    private UserTopDto userTopDto;

    //UserInfoModel
    private UserInfoDto userInfo;

    //UserEducationModel
    private List<EducationDto> education;

    private String departName;
    private String positionName;

}
