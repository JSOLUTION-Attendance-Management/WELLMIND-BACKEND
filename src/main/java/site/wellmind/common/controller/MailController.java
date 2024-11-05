package site.wellmind.common.controller;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import site.wellmind.common.domain.dto.MailDto;
import site.wellmind.common.service.MailService;

import java.io.UnsupportedEncodingException;

@RestController
@RequestMapping("/api/common")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class MailController {
    private final MailService mailService;

    @ResponseBody
    @PostMapping("/email-check")
    public String emailCheck(@RequestBody MailDto mailDto) throws MessagingException, UnsupportedEncodingException{
        String authCode=mailService.sendEmailCheckMessage(mailDto.getEmail());
        return authCode;
    }
}
