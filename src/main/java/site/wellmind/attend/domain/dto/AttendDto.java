package site.wellmind.attend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.attend.domain.vo.AttendStatus;

import java.time.LocalDateTime;

/**
 * AttendDto
 * <p>Attend Data Transfer Object</p>
 * @since 2024-11-19
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AttendDto implements BaseAttendDto {
    private AttendStatus attendStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String date;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
    private String time;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm:ss")
    private String timeSec;
}