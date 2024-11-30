package site.wellmind.user.controller;

import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.dto.TokenValidationRequestDto;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.service.MailService;
import site.wellmind.security.domain.vo.VerificationStatus;
import site.wellmind.security.service.EmailVerificationService;
import site.wellmind.security.domain.dto.LoginDto;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.service.AuthService;

/**
 * Auth Controller
 * <p>사용자 인증 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/auth</b></p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-02
 */
@Slf4j
@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;
    private final MailService mailService;
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDto dto) {
        return authService.localLogin(dto);
    }


    @PostMapping("/send-verification")
    public ResponseEntity<Messenger> sendVerification(@RequestParam(name = "email") String email) throws MessagingException {
        try {
            emailVerificationService.sendVerificationCode(email);
            return ResponseEntity.ok(Messenger.builder()
                    .message("Verification code sent successfully.")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(500).body(
                    Messenger.builder()
                            .message("Failed to send verification code.")
                            .build());
        }
    }
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        return authService.refresh(request);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        return authService.logout(request);
    }

    //이메일 인증 처리
    @PostMapping("/verify-code")
    public ResponseEntity<Messenger> verifyCode(@RequestParam(name = "email") String email, @RequestParam(name = "verifyCode") String verifyCode) {
        VerificationStatus verified = emailVerificationService.verifyCode(email, verifyCode);
        return verified==VerificationStatus.SUCCESS ?
                ResponseEntity.ok(Messenger.builder()
                        .message("verification success")
                        .state(true)
                        .build()) : verified==VerificationStatus.EXPIRED ?
                ResponseEntity.status(410).body(Messenger.builder()
                .message("expired verification code.")
                .build()):
                ResponseEntity.status(400).body(Messenger.builder()
                        .message("Invalid verification code.")
                        .build());

    }
    @PostMapping("/password/test/setup")   //save 로직에 합쳐져야 함
    public ResponseEntity<Messenger> testSetupPassword(@RequestBody PasswordSetupTestDto dto) throws MessagingException {
        try {
            mailService.sendPasswordSetupEmail(dto.getEmail(),dto.getEmployeeId());
            return ResponseEntity.ok()
                    .body(Messenger.builder()
                            .message("send password set up success").build());
        }catch (MessagingException e){
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage()).build());
        }
    }


    //비밀번호 생성 토큰 유효 검사
    @PostMapping("/password/validate-token")
    public ResponseEntity<Messenger> validatePasswordSetupToken(@RequestBody TokenValidationRequestDto request){
        return authService.validatePasswordSetupToken(request);
    }
    //비밀번호 생성
    @PostMapping("/password/setup")
    public ResponseEntity<Messenger> setupPassword(@RequestBody PasswordSetupRequestDto request){
        return authService.setupPassword(request);
    }

    @PutMapping("/modify-by-password")
    public ResponseEntity<Messenger> modifyByPassword(@RequestBody PasswordModifyRequestDto dto,HttpServletRequest request) {
        AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");

        return authService.modifyByPassword(dto,accountDto);
    }

    @PostMapping("/authenticate/code")
    public ResponseEntity<Messenger> getUserAuthenticateCode(@RequestBody UserVerifyCodeRequestDto userVerifyCodeRequestDto) throws CoolsmsException {
        return authService.startVerification(userVerifyCodeRequestDto);
    }
    @PostMapping("/authenticate/check")
    public ResponseEntity<Messenger> verigyUserAuthenticateCode(@RequestBody UserVerifyCheckRequestDto userVerifyCheckRequestDto){
        return authService.checkVerification(userVerifyCheckRequestDto);
    }
}


