package site.wellmind.user.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.MailService;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.service.UserService;

/**
 * Account Controller
 * <p>관리자와 사용자의 공통 계정 세부 정보 액세스 및 업데이트 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/account</b></p>
 * @since 2024-11-13
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Slf4j
@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AccountController {

    private final UserService userService;
    private final MailService mailService;
    @PostMapping("/register")
    public ResponseEntity<Messenger> register(@RequestBody UserDto dto) throws MessagingException {

        try {
            UserDto savedUser=userService.save(dto);
            log.info("mail register : {}, {}",savedUser.getEmail(),savedUser.getEmployeeId());
            try{
                mailService.sendPasswordSetupEmail(savedUser.getEmail(),savedUser.getEmployeeId());
            }catch (MessagingException e){
                return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                        .body(Messenger.builder()
                                .message(e.getMessage()).build());
            }
            return ResponseEntity.ok()
                    .body(Messenger.builder()
                            .message("User registration successful and password setup email sent.")
                            .data(savedUser)
                            .build());
        }catch (GlobalException e){
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
                .message("user findAll : "+SuccessStatus.OK.getMessage())
                .data(userService.findAll())
                .build());
    }

    //jwt 에서 id 꺼내는 형식으로 바꾸기
    @GetMapping("/find-by-id")
    public ResponseEntity<Messenger> findById(@RequestParam("id") Long id){
        return ResponseEntity.ok(
                Messenger.builder()
                        .message("user findById : "+SuccessStatus.OK.getMessage())
                        .data(userService.findById(id))
                        .build()
        );
    }

    @GetMapping("/findBy")
    public ResponseEntity<Page<UserDto>> findBy(@RequestParam(value = "departName",required = false) String departName,
                                                @RequestParam(value = "positionName",required = false) String positionName,
                                                @RequestParam(value = "name",required = false) String name,
                                                Pageable pageable){
        return ResponseEntity.ok(userService.findBy(departName,positionName,name,pageable));
    }

    @GetMapping("/exist-by-email")
    public ResponseEntity<Messenger> existByEmail(@RequestParam("email") String email){
        return ResponseEntity.ok(Messenger.builder()
                .message("user existByEmail : "+SuccessStatus.OK.getMessage())
                .state(userService.existByEmail(email))
                .build());
    }

    @PutMapping("/modify-by-password")
    public ResponseEntity<Messenger> modifyByPassword(@RequestParam("oldPassword") String oldPassword,
                                                      @RequestParam("newPassword") String newPassword){
        return ResponseEntity.ok(Messenger.builder()
                        .message("user modifyByPassword : "+SuccessStatus.OK.name())
                        .state(userService.modifyByPassword(oldPassword,newPassword))
                .build());
    }

    // 여러 개 데이터를 input 값으로 받고 modify

}
