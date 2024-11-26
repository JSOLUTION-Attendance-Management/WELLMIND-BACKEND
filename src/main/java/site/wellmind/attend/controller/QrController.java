package site.wellmind.attend.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import site.wellmind.attend.service.AttendService;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.attend.service.QrService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * QR Controller
 * <p>QR코드 요청을 처리하는 컨트롤러</p>
 * <p>RestController 어노테이션을 통해 Rest API 요청을 Spring Web MVC 방식으로 처리한다.</p>
 * <p>Endpoint: <b>/api/qr</b></p>
 * @since 2024-11-25
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Slf4j(topic = "AttendController")
@RestController
@RequestMapping("/api/qr")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class QrController {

    private final QrService qrService;

    @GetMapping("/create")
    public ResponseEntity<byte[]> createQrCode(
            @RequestParam(defaultValue = "250") Integer width,
            @RequestParam(defaultValue = "250") Integer height,
            HttpServletRequest request) {
        try {
            AccountDto accountDto = (AccountDto) request.getAttribute("accountDto");
            if (accountDto == null) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, ExceptionStatus.UNAUTHORIZED.getMessage());
            }

            String qrContent = qrService.generateQrContent(accountDto.getEmployeeId());
            byte[] qrCodeImage = qrService.generateQrCodeImage(qrContent, width, height);

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(qrCodeImage);
        } catch (GlobalException e) {
            log.error("QR 코드 생성 중 오류 발생", e);
            return ResponseEntity.status(e.getStatus().getHttpStatus()).build();
        }
    }
}

