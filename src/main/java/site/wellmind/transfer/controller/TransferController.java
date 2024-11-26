package site.wellmind.transfer.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.transfer.domain.dto.TransferDto;
import site.wellmind.transfer.service.TransferService;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.UserAllDto;

/**
 * TransferController
 * <p>인사이동 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/transfer</b></p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-26
 */
@Slf4j(topic = "TransferController")
@RestController
@RequestMapping("/api/transfer")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class TransferController {
    private final TransferService transferService;
    // 자신의 인사이동 기록 조회 ( 페이지네이션 )
    @GetMapping("/find-by-employeeId")
    public ResponseEntity<Page<TransferDto>> findByEmployeeId(Pageable pageable, HttpServletRequest request) {

        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        return ResponseEntity.ok(transferService.findByEmployeeId(pageable,accountDto));
    }
    // 인사이동 기록 조회(in 관리자 페이지, 페이지네이션
    @GetMapping("/find-by-all")
    public ResponseEntity<Page<TransferDto>> findByAll(@RequestParam(value = "departName", required = false) String departName,
                                                    @RequestParam(value = "positionName", required = false) String positionName,
                                                    @RequestParam(value = "name", required = false) String name,
                                                    Pageable pageable,
                                                    HttpServletRequest request) {

        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        return ResponseEntity.ok(transferService.findByAll(departName, positionName, name, pageable,accountDto));
    }
}
