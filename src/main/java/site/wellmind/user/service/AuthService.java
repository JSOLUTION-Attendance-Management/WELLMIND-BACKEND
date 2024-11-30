package site.wellmind.user.service;

import jakarta.servlet.http.HttpServletRequest;
import net.nurigo.java_sdk.exceptions.CoolsmsException;
import org.springframework.http.ResponseEntity;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.dto.TokenValidationRequestDto;
import site.wellmind.security.domain.dto.LoginDto;
import site.wellmind.user.domain.dto.*;

public interface AuthService {
    ResponseEntity<?> localLogin(LoginDto dto);
    ResponseEntity<?> refresh(HttpServletRequest request);
    ResponseEntity<?> logout(HttpServletRequest request);

    ResponseEntity<Messenger> validatePasswordSetupToken(TokenValidationRequestDto request);

    ResponseEntity<Messenger> setupPassword(PasswordSetupRequestDto request);

    ResponseEntity<Messenger> modifyByPassword(PasswordModifyRequestDto oldPassword, AccountDto newPassword);

    ResponseEntity<Messenger> startVerification(UserVerifyCodeRequestDto requestDto) throws CoolsmsException;

    ResponseEntity<Messenger> checkVerification(UserVerifyCheckRequestDto userVerifyCheckRequestDto);
}
