package site.wellmind.attend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.attend.domain.dto.RecentReportTypesDto;
import site.wellmind.attend.domain.dto.ReportDto;
import site.wellmind.attend.domain.dto.ReportListDto;
import site.wellmind.attend.domain.dto.UpdateReportDto;
import site.wellmind.attend.service.ReportService;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;

import java.util.List;

/**
 * Report Controller
 * <p>웰니스 리포트 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/report</b></p>
 * @since 2024-11-28
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Slf4j(topic = "ReportController")
@RestController
@RequestMapping("/api/report")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ReportController {
    private final ReportService reportService;

    @GetMapping("/recent")
    public ResponseEntity<Messenger> viewRecentTypes(
            @RequestParam(value = "recentCount", required = false) Integer recentCount,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            List<RecentReportTypesDto> reportRecords = reportService.viewRecent(accountDto, recentCount);
            return ResponseEntity.ok(Messenger.builder()
                    .message("recent report type view : " + SuccessStatus.OK.getMessage())
                    .data(reportRecords)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/cal")
    public ResponseEntity<Messenger> viewCal(
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            List<ReportListDto> reportRecords = reportService.viewCal(accountDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("report cal view : " + SuccessStatus.OK.getMessage())
                    .data(reportRecords)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/list")
    public ResponseEntity<Messenger> viewList(
            Pageable pageable,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            if (!accountDto.isAdmin()) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            Page<ReportListDto> reportRecords = reportService.viewAd(accountDto, pageable);
            return ResponseEntity.ok(Messenger.builder()
                    .message("report list view : " + SuccessStatus.OK.getMessage())
                    .data(reportRecords)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/view/{reportId}")
    public ResponseEntity<Messenger> viewDetail(
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @PathVariable Long reportId,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            ReportDto reportDetail = reportService.viewDetail(employeeId, reportId, accountDto);

            return ResponseEntity.ok(Messenger.builder()
                    .message("report detail view : " + SuccessStatus.OK.getMessage())
                    .data(reportDetail)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/update/{reportId}")
    public ResponseEntity<Messenger> updateReport(
            @PathVariable Long reportId,
            @RequestBody UpdateReportDto updateReportDto,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            if (!accountDto.isAdmin()) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            reportService.updateReport(reportId, updateReportDto, accountDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("report update : " + SuccessStatus.OK.getMessage())
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @PutMapping("/mark-sent/{reportId}")
    public ResponseEntity<Messenger> markReportAsSent(
            @PathVariable Long reportId,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            if (!accountDto.isAdmin()) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            reportService.markReportAsSent(reportId, accountDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Report mark as sent : " + SuccessStatus.OK.getMessage())
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}
