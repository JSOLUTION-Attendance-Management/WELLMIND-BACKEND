package site.wellmind.attend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import site.wellmind.attend.domain.vo.AttendStatus;

/**
 * SimpleAttendDto
 * <p>Attend Status Data Transfer Object</p>
 * @since 2024-11-19
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SimpleAttendDto implements BaseAttendDto {
    private AttendStatus attendStatus;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String date;
}