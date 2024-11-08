package site.wellmind.user.service;

import org.springframework.http.ResponseEntity;
import site.wellmind.user.domain.dto.LoginDto;
import site.wellmind.user.domain.dto.ProfileDto;

public interface AuthService {
    ResponseEntity<?> localLogin(LoginDto dto);
    ResponseEntity<?> refresh(String refreshToken);
    ResponseEntity<?> logout(String refreshToken);
}
