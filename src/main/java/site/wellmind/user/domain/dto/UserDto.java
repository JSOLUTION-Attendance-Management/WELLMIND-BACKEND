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
 * <p>User Data Transfer Object</p>
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

    private Long id;
    private String employeeId;
    private String email;
    private String password;
    private String name;
    private String phoneNum;
    private String regNumberFor;
    private String regNumberLat;
    private boolean deleteFlag;
    private String authType;
    private AdminRole authUserLevelCodeId;

    //UserInfoModel
    private UserInfoDto userInfo;

    //UserEducationModel
    private List<EducationDto> education;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;

}
