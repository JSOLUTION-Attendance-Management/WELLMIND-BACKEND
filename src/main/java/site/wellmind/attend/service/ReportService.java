package site.wellmind.attend.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import site.wellmind.attend.domain.dto.RecentReportTypesDto;
import site.wellmind.attend.domain.dto.ReportDto;
import site.wellmind.attend.domain.dto.ReportListDto;
import site.wellmind.attend.domain.dto.UpdateReportDto;
import site.wellmind.attend.domain.model.AttendReportModel;
import site.wellmind.user.domain.dto.AccountDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ReportService
 * <p>Report Service Interface</p>
 *
 * @version 1.0
 * @see ReportDto
 * @since 2024-11-20
 */
public interface ReportService {
    default ReportDto entityToDtoReportRecord(AttendReportModel model) {
        LocalDateTime regDate = model.getRegDate();
        LocalDateTime modDate = model.getModDate();
        return ReportDto.builder()
                .reportId(model.getId())
                .registeredDate(regDate != null ? regDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .modifiedDate(modDate != null ? modDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .aiComment(model.getAiComment())
                .managerComment(model.getManagerComment())
                .reportType(model.getUserType())
                .isAdmin(model.getIsAdmin())
                .isSent(model.getIsSent())
                .build();
    }

    default ReportListDto entityToDtoReportListRecord(AttendReportModel model) {
        LocalDateTime regDate = model.getRegDate();

        return ReportListDto.builder()
                .reportId(model.getId())
                .registeredDate(regDate != null ? regDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .reportType(model.getUserType())
                .isSent(model.getIsSent())
                .build();
    }

    default RecentReportTypesDto entityToDtoRecentReportTypes(AttendReportModel model) {
        LocalDateTime regDate = model.getRegDate();
        LocalDateTime modDate = model.getModDate();

        return RecentReportTypesDto.builder()
                .reportId(model.getId())
                .registeredDate(regDate != null ? regDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .modifiedDate(modDate != null ? modDate.format(DateTimeFormatter.ISO_LOCAL_DATE) : null)
                .reportType(model.getUserType())
                .build();
    }

    List<RecentReportTypesDto> viewRecent(AccountDto accountDto, Integer recentCount);

    List<ReportListDto> viewCal(AccountDto accountDto);

    Page<ReportListDto> viewAd(AccountDto accountDto, Pageable pageable);

    ReportDto viewDetail(String employeeId, Long reportId, AccountDto accountDto);

    void updateReport(Long reportId, UpdateReportDto updateReportDto, AccountDto accountDto);

    void markReportAsSent(Long reportId, AccountDto accountDto);
}