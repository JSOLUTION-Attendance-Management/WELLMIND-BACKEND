package site.wellmind.attend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ReportListDto
 * <p>Report List Data Transfer Object</p>
 * @since 2024-11-28
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportListDto {
    private Long reportId;

    private String reportType;

    private String reportedEmployeeId;

    private String reportedEmployeeName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String registeredDate;

    private boolean isSent;
}