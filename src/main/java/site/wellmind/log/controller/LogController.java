package site.wellmind.log.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.log.domain.dto.LogViewDto;
import site.wellmind.log.domain.dto.ViewReasonDto;
import site.wellmind.log.service.LogViewService;
import site.wellmind.security.annotation.CurrentAccount;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.service.AccountService;

import java.util.Optional;

/**
 * Log Controller
 * <p>사용자 CRUD 관련 요청 log를 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/log</b></p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-19
 */
@Slf4j(topic = "LogController")
@RestController
@RequestMapping("/api/log")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class LogController {
    private final LogViewService viewLogService;
    private final AccountService accountService;

    @PostMapping("/view/register")
    public ResponseEntity<Messenger> registerViewLog(@RequestBody ViewReasonDto viewReasonDto, @CurrentAccount AccountDto accountDto){
        String viewedId=viewReasonDto.getViewerId();

        if(viewedId.isEmpty() || viewReasonDto.getViewReason().isEmpty()){
            return ResponseEntity.status(ExceptionStatus.INVALID_INPUT.getHttpStatus())
                    .body(Messenger.builder()
                            .message(ExceptionStatus.BAD_REQUEST.getMessage()).build());
        }

        Optional<UserTopModel> userTopModel=accountService.findUserByEmployeeId(viewedId);
        if(userTopModel.isEmpty()){
            return ResponseEntity.status(ExceptionStatus.USER_NOT_FOUND.getHttpStatus())
                    .body(Messenger.builder()
                            .message(ExceptionStatus.USER_NOT_FOUND.getMessage()).build());

        }

        Optional<AdminTopModel> adminTopModel=accountService.findAdminByEmployeeId(accountDto.getEmployeeId());
        if(adminTopModel.isEmpty()){
            return ResponseEntity.status(ExceptionStatus.ADMIN_NOT_FOUND.getHttpStatus())
                    .body(Messenger.builder()
                            .message(ExceptionStatus.ADMIN_NOT_FOUND.getMessage()).build());

        }

        LogViewDto savedLog=viewLogService.save(LogViewDto.builder()
                .viewReason(viewReasonDto.getViewReason())
                .userId(userTopModel.get())
                .adminId(adminTopModel.get())
                .build());

        return ResponseEntity.ok(Messenger.builder()
                .state(savedLog!=null).build());
    }

}
