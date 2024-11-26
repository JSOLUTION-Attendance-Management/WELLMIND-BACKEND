package site.wellmind.attend.service;

import site.wellmind.common.exception.GlobalException;

public interface QrService {
    String generateQrContent(String employeeId);
    byte[] generateQrCodeImage(String content, int width, int height) throws GlobalException;
}
