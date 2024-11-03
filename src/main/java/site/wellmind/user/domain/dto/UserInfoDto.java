package site.wellmind.user.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.vo.AddressVO;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
    private AddressVO address;
    private String hobby;
    private String significant;
    private String photo;
    private boolean isLong;
}
