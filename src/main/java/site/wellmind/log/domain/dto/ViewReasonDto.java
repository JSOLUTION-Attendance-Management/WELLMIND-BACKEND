package site.wellmind.log.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.security.annotation.CurrentAccount;
import site.wellmind.user.domain.dto.AccountDto;

/**
 * ViewReasonDto
 * <p>View Reason Transfer Object</p>
 * @since 2024-11-19
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ViewReasonDto {
    @NotNull(message = "조회 대상 사원 번호은 필수입니다.")
    private String viewedEmployeeId;  //조회할 대상의 사원번호

    @NotNull(message = "조회 사유 입력은 필수입니다.")
    private String viewReason;

}
