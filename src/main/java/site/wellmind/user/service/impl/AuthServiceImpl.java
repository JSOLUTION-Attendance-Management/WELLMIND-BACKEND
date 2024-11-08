package site.wellmind.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import site.wellmind.user.domain.dto.LoginDto;
import site.wellmind.user.domain.dto.ProfileDto;
import site.wellmind.user.service.AuthService;
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    @Override
    public ResponseEntity<?> localLogin(LoginDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<?> refresh(String refreshToken) {
        return null;
    }

    @Override
    public ResponseEntity<?> logout(String refreshToken) {
        return null;
    }
}
