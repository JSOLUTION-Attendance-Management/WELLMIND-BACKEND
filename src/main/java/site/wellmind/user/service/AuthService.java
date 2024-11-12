package site.wellmind.user.service;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import site.wellmind.security.domain.dto.LoginDto;

public interface AuthService {
    ResponseEntity<?> localLogin(LoginDto dto);
    ResponseEntity<?> refresh(HttpServletRequest request);
    ResponseEntity<?> logout(HttpServletRequest request);
}
