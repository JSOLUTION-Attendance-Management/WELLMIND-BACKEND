package site.wellmind.attend.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * QrCodeReponseDto
 * <p>QrCode Response Data Transfer Object</p>
 * @since 2024-11-25
 * @version 1.0
 * @author Jihyeon Park(jihyeon2525)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QrCodeResponseDto {
    private String qrToken;
    private LocalDateTime qrTokenExpire;
}