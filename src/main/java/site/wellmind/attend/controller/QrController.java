package site.wellmind.attend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.attend.domain.dto.QrCodeResponseDto;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.SuccessStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.attend.service.QrService;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

/**
 * Qr Controller
 * <p>Qr코드 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/qr</b></p>
 * @since 2024-11-25
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Slf4j(topic = "QrController")
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QrController {

    private final QrService qrService;

    @PostMapping("/create")
    public ResponseEntity<Messenger> createQrCode(
            @RequestParam(defaultValue = "384") Integer width,
            @RequestParam(defaultValue = "384") Integer height,
            HttpServletRequest request) {
        try {
            LocalDateTime timeNow = LocalDateTime.now();

            AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
            if (accountDto == null) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }

            QrCodeResponseDto qrCodeResponse = qrService.createAndSaveQrCode(accountDto, width, height, timeNow);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Messenger.builder()
                            .message("create QR : " + SuccessStatus.OK.getMessage())
                            .data(qrCodeResponse)
                            .build());
        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        }
    }
}