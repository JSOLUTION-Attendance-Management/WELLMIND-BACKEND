package site.wellmind.log.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.*;
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
 * LogDeleteDto
 * <p>Log Delete Object</p>
 * @since 2024-11-24
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class LogDeleteDto {

    private Long id;
    private DeleteStatus deleteType;
    private String deleteReason;

    private AdminTopModel deleterId;
    private List<LogArchiveDeleteDetailModel> logDeleteDetailLogIds;

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime regDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS")
    private LocalDateTime modDate;
}
