package site.wellmind.attend.service.impl;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import site.wellmind.attend.service.QrService;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class QrServiceImpl implements QrService {

    @Override
    public String generateQrContent(String employeeId) {
        LocalDateTime now = LocalDateTime.now();
        return String.format("%s|%s", employeeId, now.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));
    }

    @Override
    public byte[] generateQrCodeImage(String content, int width, int height) throws GlobalException {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(content, BarcodeFormat.QR_CODE, width, height);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            return pngOutputStream.toByteArray();
        } catch (WriterException | IOException e) {
            throw new GlobalException(ExceptionStatus.INTERNAL_SERVER_ERROR, "QR 코드 생성에 실패했습니다.");
        }
    }
}