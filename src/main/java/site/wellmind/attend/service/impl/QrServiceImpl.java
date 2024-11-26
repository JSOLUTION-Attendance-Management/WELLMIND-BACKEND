package site.wellmind.attend.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import site.wellmind.attend.domain.dto.QrCodeResponseDto;
import site.wellmind.attend.domain.model.AttendQrModel;
import site.wellmind.attend.repository.AttendQrRepository;
import site.wellmind.attend.service.QrService;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Qr Service Implementation
 * <p>Qr Service Implementation</p>
 *
 * @author Jihyeon Park(jihyeon2525)
 * @version 1.0
 * @see QrService
 * @see AttendQrRepository
 * @since 2024-11-25
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "QrServiceImpl")
public class QrServiceImpl implements QrService {

    @Autowired
    private EntityManager entityManager;

    private final AttendQrRepository attendQrRepository;
    private final UserTopRepository userTopRepository;
    private final AdminTopRepository adminTopRepository;

    @Override
    public String generateQrContent(AccountDto accountDto, String employeeId, LocalDateTime expireTime) {
        boolean isAdmin = accountDto.isAdmin();
        SecureRandom secureRandom = new SecureRandom();
        byte[] randomBytes = new byte[16]; // 128비트 난수
        secureRandom.nextBytes(randomBytes);
        String randomString = Base64.getEncoder().encodeToString(randomBytes);

        return String.format("%s|%s|%s|%s", employeeId, isAdmin, expireTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME), randomString);
    }

    @Override
    public byte[] generateQrCodeImage(String content, int widthHeight) throws GlobalException {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, widthHeight, widthHeight);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, ExceptionStatus.INTERNAL_SERVER_ERROR.getMessage());
        }
    }

    @Override
    @Transactional
    public QrCodeResponseDto createAndSaveQrCode(AccountDto accountDto, int widthHeight, LocalDateTime timeNow) throws GlobalException {
        String employeeId = accountDto.getEmployeeId();

        attendQrRepository.updatePreviousQrCodes(employeeId);
        entityManager.flush();

        LocalDateTime expire = timeNow.plusMinutes(5); // 5분 후 만료

        String qrContent = generateQrContent(accountDto, employeeId, expire);
        byte[] qrCodeImage = generateQrCodeImage(qrContent, widthHeight);
        String qrCodeBase64 = Base64.getEncoder().encodeToString(qrCodeImage);

        AttendQrModel newQrCode = AttendQrModel.builder()
                .qrToken(qrCodeBase64)
                .qrTokenExpire(expire)
                .qrTokenisLast(true)
                .build();

        if (accountDto.isAdmin()) {
            AdminTopModel admin = adminTopRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));
            newQrCode.setAdminId(admin);
        } else {
            UserTopModel user = userTopRepository.findByEmployeeId(employeeId)
                    .orElseThrow(() -> new GlobalException(ExceptionStatus.USER_NOT_FOUND, ExceptionStatus.USER_NOT_FOUND.getMessage()));
            newQrCode.setUserId(user);
        }

        AttendQrModel savedQrCode = attendQrRepository.save(newQrCode);

        return QrCodeResponseDto.builder()
                .qrToken(savedQrCode.getQrToken())
                .qrTokenExpire(savedQrCode.getQrTokenExpire())
                .build();
    }
}