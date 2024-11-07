package site.wellmind.common.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.MailDto;
import site.wellmind.common.service.MailService;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.UserService;

import java.io.UnsupportedEncodingException;

/**
 * MailController
 * <p>SMTP 관련 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/common</b></p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-06
 */
@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MailController {
    private final MailService mailService;


}
