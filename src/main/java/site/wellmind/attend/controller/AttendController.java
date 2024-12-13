package site.wellmind.attend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.attend.domain.dto.BaseAttendDto;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.attend.domain.dto.RecentAttendDto;
import site.wellmind.attend.service.AttendService;
import site.wellmind.user.domain.dto.AccountDto;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDate;
import java.util.List;

/**
 * Attend Controller
 * <p>출결기록 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/attend</b></p>
 * @since 2024-11-19
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Slf4j(topic = "AttendController")
@RestController
@RequestMapping("/api/attend")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AttendController {

    private final AttendService attendService;

    @GetMapping("/find-by")
    public ResponseEntity<Messenger> findBy(
            @RequestParam(value = "employeeId", required = false) String employeeId,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            Pageable pageable,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        try {
            Page<BaseAttendDto> attendRecords = attendService.findBy(employeeId, accountDto, pageable, startDate, endDate);
            return ResponseEntity.ok(Messenger.builder()
                    .message("attend findBy : " + SuccessStatus.OK.getMessage())
                    .data(attendRecords)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }

    @GetMapping("/recent-attendances")
    public ResponseEntity<Messenger> findRecentAttendances(
            @RequestParam(value = "recentCount", required = true) Integer recentCount,
            HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        try {
            List<RecentAttendDto> recentAttendances = attendService.findRecentAttendances(accountDto, recentCount);
            return ResponseEntity.ok(Messenger.builder()
                    .message("recent attend : " + SuccessStatus.OK.getMessage())
                    .data(recentAttendances)
                    .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}