package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.vo.AddressVO;

import java.time.LocalDateTime;
/**
 * ProfileDto
 * <p>Profile Data Transfer Object</p>
 * @since 2024-11-21
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileDto {
    private String email;
    private String name;
    private String phoneNum;
    private String authType;
    private String employeeId;

    private String photo;
    private AddressVO address;

    private String departName;
    private String positionName;
}
