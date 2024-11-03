package site.wellmind.user.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.user.domain.dto.UserDto;
import site.wellmind.user.service.UserService;

import java.util.Map;

/**
 * User Controller
 * <p>사용자 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/user</b></p>
 * @since 2024-10-08
 * @version 1.0
 * @author Yuri Seok(tjrdbfl)
 */
@Slf4j
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class UserController {

    private final UserService userService;
    @PostMapping("/register")
    public ResponseEntity<Messenger> register(@RequestBody UserDto dto) {

        return ResponseEntity.ok(Messenger
                .builder()
                .message("auth join : " + SuccessStatus.OK.getMessage())
                .data(userService.save(dto))
                .build());
    }
    @GetMapping("/register/exist-by-employeeId")
    public ResponseEntity<Messenger> existsByEmployeeId(@RequestParam("employeeId") String employeeId) {
        return ResponseEntity.ok(Messenger
                .builder()
                .message("user existByEmployeeId 조회 결과")
                .state(userService.existByEmployeeId(employeeId))
                .build());
    }

}
