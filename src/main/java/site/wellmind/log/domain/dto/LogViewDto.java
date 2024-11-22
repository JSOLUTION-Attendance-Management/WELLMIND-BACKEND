package site.wellmind.log.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;

import java.time.LocalDateTime;

/**
 * LogViewDto
 * <p>Log Transfer Object</p>
 * @since 2024-11-19
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogViewDto {
    private Long id;
    private String viewReason;

    private String viewedId;
    private AdminTopModel viewerId;  //조회한 사람

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;
}
