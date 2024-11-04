package site.wellmind.user.controller;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

}
