package site.wellmind.user.controller;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.MailService;
import site.wellmind.security.annotation.CurrentAccount;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.service.AccountService;

/**
 * User Controller
 * <p>사용자 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/user</b></p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-10-08
 */
@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

    private final AccountService userService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<Messenger> register(@RequestBody UserDto dto) throws MessagingException {

        try {
            UserDto savedUser = userService.save(dto);
            log.info("mail register : {}, {}", savedUser.getEmail(), savedUser.getEmployeeId());
            try {
                mailService.sendPasswordSetupEmail(savedUser.getEmail(), savedUser.getEmployeeId());
            } catch (MessagingException e) {
                return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                        .body(Messenger.builder()
                                .message(e.getMessage()).build());
            }
            return ResponseEntity.ok()
                    .body(Messenger.builder()
                            .message("User registration successful and password setup email sent.")
                            .data(savedUser)
                            .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage()).build());
        }
    }

    @GetMapping("/exist-by-employeeId")
    public ResponseEntity<Messenger> existByEmployeeId(@RequestParam("employeeId") String employeeId) {
        return ResponseEntity.ok(Messenger
                .builder()
                .message("user existByEmployeeId 조회 결과")
                .state(userService.existByEmployeeId(employeeId))
                .build());
    }

    @GetMapping("/find-all")
    public ResponseEntity<Messenger> findAll() {
        return ResponseEntity.ok(Messenger
                .builder()
                .message("user findAll : " + SuccessStatus.OK.getMessage())
                .data(userService.findAll())
                .build());
    }

    //jwt 에서 id 꺼내는 형식으로 바꾸기
    @GetMapping("/find-by-id")
    public ResponseEntity<Messenger> findById(
            @RequestParam(value = "id", required = false) Long id, HttpServletRequest request
    ) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        boolean isAdmin = accountDto.isAdmin();
        Long currentAccountId=accountDto.getAccountId();

        if (!isAdmin && id != null) {
            return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus()).
                    body(Messenger.builder()
                            .message("User can only access their own information.")
                            .build());
        }

        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : " + SuccessStatus.OK.getMessage())
                        .data(userService.findById(id, currentAccountId, isAdmin))
                        .build()
        );
    }

    @GetMapping("/find-by")
    public ResponseEntity<Page<UserDto>> findBy(@RequestParam(value = "departName", required = false) String departName,
                                                @RequestParam(value = "positionName", required = false) String positionName,
                                                @RequestParam(value = "name", required = false) String name,
                                                Pageable pageable) {
        return ResponseEntity.ok(userService.findBy(departName, positionName, name, pageable));
    }

    @GetMapping("/exist-by-email")
    public ResponseEntity<Messenger> existByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(Messenger.builder()
                .message("user existByEmail : " + SuccessStatus.OK.getMessage())
                .state(userService.existByEmail(email))
                .build());
    }

    @PutMapping("/modify-by-password")
    public ResponseEntity<Messenger> modifyByPassword(@RequestParam("oldPassword") String oldPassword,
                                                      @RequestParam("newPassword") String newPassword) {
        return ResponseEntity.ok(Messenger.builder()
                .message("user modifyByPassword : " + SuccessStatus.OK.name())
                .state(userService.modifyByPassword(oldPassword, newPassword))
                .build());
    }

    // 여러 개 데이터를 input 값으로 받고 modify

}
