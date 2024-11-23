package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.common.domain.vo.AdminRole;

import java.time.LocalDateTime;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDto {

    private UserTopDto userTopDto;

    //UserInfoModel
    private UserInfoDto userInfo;

    //UserEducationModel
    private List<EducationDto> education;

    private String departName;
    private String positionName;

}
