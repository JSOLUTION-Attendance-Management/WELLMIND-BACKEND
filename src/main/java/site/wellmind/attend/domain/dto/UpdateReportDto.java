package site.wellmind.attend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * UpdateReportDto
 * <p>View Report Data Transfer Object</p>
 * @since 2024-11-28
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateReportDto {
    private String aiComment;

    private String managerComment;

}
