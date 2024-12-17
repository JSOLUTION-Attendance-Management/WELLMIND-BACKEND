package site.wellmind.attend.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * ViewReportDto
 * <p>View Report Data Transfer Object</p>
 * @since 2024-11-28
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReportDto {
    private Long reportId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String registeredDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private String modifiedDate;

    private String aiComment;

    private String managerComment;

    private String reportType;

    private Long reportedId;

    private String reportedEmployeeId;

    private String reportedEmployeeName;

    private boolean reportedEmployeeIsLong;

    private boolean isAdmin;

    private boolean isSent;

    private List<List<String>> keywords;

    private String departmentAndPosition;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private LocalDateTime hireDate;
    private String email;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy.MM.dd")
    private String birthDate;

}
