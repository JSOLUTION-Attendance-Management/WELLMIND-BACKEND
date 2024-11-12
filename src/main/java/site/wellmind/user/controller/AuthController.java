package site.wellmind.user.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.security.domain.vo.VerificationStatus;
import site.wellmind.security.service.EmailVerificationService;
import site.wellmind.security.domain.dto.LoginDto;
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
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class AuthController {
    private final AuthService authService;
    private final EmailVerificationService emailVerificationService;

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

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(HttpServletRequest request){
        return authService.refresh(request);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request){
        return authService.logout(request);
    }

}


