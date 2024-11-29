package site.wellmind.attend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.attend.domain.dto.ReportDto;
import site.wellmind.attend.domain.dto.ReportListDto;
import site.wellmind.attend.domain.dto.UpdateReportDto;
import site.wellmind.attend.service.ReportService;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;

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

    @GetMapping("/list")
    public ResponseEntity<Messenger> viewList(
            @RequestParam(value = "employeeId", required = false) String employeeId,
            Pageable pageable,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        try {
            if (!accountDto.isAdmin() && employeeId != null) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }
            Page<ReportListDto> reportRecords = reportService.view(employeeId, accountDto, pageable);
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

            // 관리자가 아니고, 리포트의 reportedId가 현재 사용자의 ID와 다르면 접근 거부
            if (!accountDto.isAdmin() && (!reportDetail.getReportedId().equals(accountDto.getAccountId()) || reportDetail.isAdmin())) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }

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
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "관리자만 리포트를 수정할 수 있습니다.");
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
