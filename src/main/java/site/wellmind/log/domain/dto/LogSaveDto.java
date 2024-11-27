package site.wellmind.log.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.log.domain.model.LogArchiveDeleteDetailModel;
import site.wellmind.log.domain.vo.DeleteStatus;
import site.wellmind.user.domain.model.AdminTopModel;

import java.time.LocalDateTime;
import java.util.List;

/**
 * LogSaveDto
 * @since 2024-11-27
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogSaveDto {
    private Long id;
    private String savedEmployeeId;
    private AdminTopModel saverId;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;
}
