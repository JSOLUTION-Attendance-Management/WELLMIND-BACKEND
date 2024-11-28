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
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.user.domain.dto.*;
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
@Slf4j(topic = "AccountController")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

    private final AccountService accountService;
    private final MailService mailService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/register")
    public ResponseEntity<Messenger> register(@RequestBody UserAllDto dto) throws MessagingException {

        try {
            UserAllDto savedUser = (UserAllDto) accountService.save(dto);
            log.info("mail register : {}, {}", savedUser.getUserTopDto().getEmail(), savedUser.getUserTopDto().getEmployeeId());
            try {
                mailService.sendPasswordSetupEmail(savedUser.getUserTopDto().getEmail(), savedUser.getUserTopDto().getEmployeeId());
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
    @PostMapping("/register/profile")
    public ResponseEntity<Messenger> registerProfile(@RequestBody RegProfileDto dto,HttpServletRequest request ) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        if(!accountDto.isAdmin()){
            return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Only Admin can register profile")
                            .build());
        }
        try{
            accountService.registerProfile(dto,accountDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message(SuccessStatus.OK.getMessage()).build());
        }catch (Exception e){
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Error in register Profile info: "+e)
                            .build());
        }
    }

    @PostMapping("/register/detail")
    public ResponseEntity<Messenger> registerDetail(@RequestBody UserDetailDto dto, HttpServletRequest request) throws MessagingException {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        try{
            accountService.registerDetail(dto,accountDto);
            return ResponseEntity.ok(Messenger.builder()
                    .message(SuccessStatus.OK.getMessage()).build());
        }catch (Exception e){
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Error in register Detail info: "+e)
                            .build());
        }
    }


    @GetMapping("/exist-by-employeeId")
    public ResponseEntity<Messenger> existByEmployeeId(HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        return ResponseEntity.ok(Messenger
                .builder()
                .message("user existByEmployeeId 조회 결과")
                .state(accountService.existByEmployeeId(accountDto))
                .build());
    }


    @GetMapping("/find-by-id")
    public ResponseEntity<Messenger> findById(
            @RequestParam(value = "employeeId", required = false) String employeeId, HttpServletRequest request
    ) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        boolean isAdmin = accountDto.isAdmin();
        log.info("AccountController isAdmin :{}", isAdmin);

        if (!isAdmin && employeeId != null) {
            return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus()).
                    body(Messenger.builder()
                            .message("User can only access their own information.")
                            .build());
        }

        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : " + SuccessStatus.OK.getMessage())
                        .data(accountService.findById(employeeId, accountDto))
                        .build()
        );
    }

    @GetMapping("/find-by-id/profile")
    public ResponseEntity<Messenger> profileFindById(HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        boolean isAdmin = accountDto.isAdmin();
        Long currentAccountId = accountDto.getAccountId();

        log.info("AccountController isAdmin :{}", isAdmin);

        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : " + SuccessStatus.OK.getMessage())
                        .data(accountService.findProfileById(currentAccountId, isAdmin))
                        .build()
        );
    }

    @GetMapping("/find-by-id/detail")
    public ResponseEntity<Messenger> detailFindById(HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        boolean isAdmin = accountDto.isAdmin();
        Long currentAccountId = accountDto.getAccountId();

        log.info("AccountController isAdmin :{}", isAdmin);

        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : " + SuccessStatus.OK.getMessage())
                        .data(accountService.findDetailById(currentAccountId, isAdmin))
                        .build()
        );
    }

    @GetMapping("/find-by")
    public ResponseEntity<Page<UserAllDto>> findBy(@RequestParam(value = "departName", required = false) String departName,
                                                   @RequestParam(value = "positionName", required = false) String positionName,
                                                   @RequestParam(value = "name", required = false) String name,
                                                   Pageable pageable) {
        return ResponseEntity.ok(accountService.findBy(departName, positionName, name, pageable));
    }

    @GetMapping("/exist-by-email")
    public ResponseEntity<Messenger> existByEmail(@RequestParam("email") String email) {
        return ResponseEntity.ok(Messenger.builder()
                .message("user existByEmail : " + SuccessStatus.OK.getMessage())
                .state(accountService.existByEmail(email))
                .build());
    }

    @PutMapping("/modify-by-password")
    public ResponseEntity<Messenger> modifyByPassword(@RequestParam("oldPassword") String oldPassword,
                                                      @RequestParam("newPassword") String newPassword) {
        return ResponseEntity.ok(Messenger.builder()
                .message("user modifyByPassword : " + SuccessStatus.OK.name())
                .state(accountService.modifyByPassword(oldPassword, newPassword))
                .build());
    }

    // 여러 개 데이터를 input 값으로 받고 modify
    @PutMapping("/modify-by-id")
    public ResponseEntity<Messenger> modifyById(@RequestBody UserAllDto dto, HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        boolean isAdmin = accountDto.isAdmin();
        String currentEmployeeId = accountDto.getEmployeeId();

        if (!isAdmin && !dto.getUserTopDto().getEmployeeId().equals(currentEmployeeId)) {
            return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus()).
                    body(Messenger.builder()
                            .message("User can only modify their own information.")
                            .build());
        }

        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : " + SuccessStatus.OK.getMessage())
                        .data(accountService.modify(dto, accountDto))
                        .build()
        );
    }

    @DeleteMapping("/delete-by-id")
    public ResponseEntity<Messenger> deleteById(@RequestBody UserLogRequestDto userLogRequestDto, HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
        if (!accountDto.isAdmin()) {
            return ResponseEntity.status(ExceptionStatus.NO_PERMISSION.getHttpStatus())
                    .body(Messenger.builder()
                            .message(ExceptionStatus.NO_PERMISSION.getMessage())
                            .build());
        }

        accountService.deleteById(userLogRequestDto,accountDto);

        return ResponseEntity.ok(Messenger.builder()
                .message("User deleted successfully.")
                .state(true).build());
    }
}
