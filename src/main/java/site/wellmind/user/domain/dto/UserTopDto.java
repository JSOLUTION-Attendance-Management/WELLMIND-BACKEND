package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.common.domain.vo.AdminRole;
import site.wellmind.user.domain.model.AccountRoleModel;
import site.wellmind.user.domain.model.UserInfoModel;

import java.time.LocalDateTime;

/**
 * UserTopDto
 * <p>User Top Data Transfer Object</p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserTopDto {
    private Long id;
    private String employeeId;
    private String email;
    private String name;
    private String phoneNum;
    private String regNumberFor;
    private String regNumberLat;
    private boolean deleteFlag;
    private String authType;
    private String authAdminLevelCodeId;

    //private AccountRoleModel role;
    private Long roleId;
    private Long userInfoId;
    private Long userSignificantId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd")
    private LocalDateTime modDate;

}
