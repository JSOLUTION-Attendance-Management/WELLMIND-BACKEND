package site.wellmind.user.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.user.domain.dto.LoginDto;
import site.wellmind.user.domain.dto.ProfileDto;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.service.UserService;

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
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Messenger> login(@RequestBody LoginDto dto){
        return ResponseEntity.ok(
                Messenger.builder()
                        .message("auth login : "+SuccessStatus.OK.getMessage())
                        .data(userService.login(dto))
                        .build());
    }
}
