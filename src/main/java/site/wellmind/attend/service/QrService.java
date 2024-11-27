package site.wellmind.attend.service;

import site.wellmind.attend.domain.dto.QrCodeResponseDto;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.user.domain.dto.AccountDto;

import java.time.LocalDateTime;

/**
 * QrService
 * <p>Qr Service Interface</p>
 *
 * @version 1.0
 * @see QrCodeResponseDto
 * @since 2024-11-25
 */

public interface QrService {
    String generateQrContent(AccountDto accountDto, String employeeId, LocalDateTime expireTime);
    byte[] generateQrCodeImage(String content, int widthHeight) throws GlobalException;
    QrCodeResponseDto createAndSaveQrCode(AccountDto accountDto, int widthHeight, LocalDateTime timeNow) throws GlobalException;
}