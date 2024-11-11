package site.wellmind.user.service;

import org.springframework.http.ResponseEntity;
import site.wellmind.security.domain.dto.LoginDto;

public interface AuthService {
    ResponseEntity<?> localLogin(LoginDto dto);
    ResponseEntity<?> refresh(String refreshToken);
    ResponseEntity<?> logout(String refreshToken);
}
