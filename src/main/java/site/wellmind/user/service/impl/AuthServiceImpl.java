package site.wellmind.user.service.impl;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.dto.TokenValidationRequestDto;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.log.domain.model.LogArchiveLoginModel;
import site.wellmind.log.repository.LogArchiveLoginRepository;
import site.wellmind.security.domain.model.AccountTokenModel;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.domain.vo.TokenStatus;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.domain.dto.LoginDto;
import site.wellmind.security.provider.PasswordTokenProvider;
import site.wellmind.security.repository.AccountTokenRepository;
import site.wellmind.user.domain.dto.PasswordSetupRequestDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.AuthService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j(topic = "AuthServiceImpl")
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordTokenProvider passwordTokenProvider;

    private final UserTopRepository userTopRepository;
    private final AdminTopRepository adminTopRepository;
    private final AccountTokenRepository accountTokenRepository;
    private final LogArchiveLoginRepository logArchiveLoginRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public ResponseEntity<Messenger> localLogin(LoginDto dto) {

        String encoder=passwordEncoder.encode(dto.getPassword());

        String employeeId = dto.getEmployeeId();
        String password = dto.getPassword();

        log.info("encoder : {}",encoder);
        log.info("encoder : {}",passwordEncoder.matches(password,encoder));

        Optional<AccountTokenModel> accountTokenModel = accountTokenRepository.findByEmployeeIdAndTokenStatus(employeeId, TokenStatus.VALID);
        if (!accountTokenModel.isEmpty()) {
            throw new GlobalException(ExceptionStatus.ALREADY_LOGGED_IN, ExceptionStatus.ALREADY_LOGGED_IN.getMessage());
        }

        Optional<UserTopModel> user = userTopRepository.findByEmployeeId(employeeId);
        Optional<AdminTopModel> admin = adminTopRepository.findByEmployeeId(employeeId);

        String accessToken = null;
        String refreshToken = null;

        if (user.isEmpty() && admin.isEmpty()) {
            throw new GlobalException(ExceptionStatus.USER_NOT_FOUND);
        } else if (user.isPresent()) {

            if(user.get().getPasswordExpiry()!=null && user.get().getPasswordExpiry().isBefore(LocalDateTime.now())){
                userTopRepository.updatePasswordExpiry(user.get().getEmployeeId());
            }
            if(!passwordEncoder.matches(password,user.get().getPassword())){
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }
            PrincipalUserDetails userDetails = new PrincipalUserDetails(user.get());
            accessToken = jwtTokenProvider.generateToken(userDetails, false);
            refreshToken = jwtTokenProvider.generateToken(userDetails, true);

        } else {

            if(admin.get().getPasswordExpiry()!=null && admin.get().getPasswordExpiry().isBefore(LocalDateTime.now())){
                adminTopRepository.updatePasswordExpiry(admin.get().getEmployeeId());
            }
            if(!passwordEncoder.matches(password,admin.get().getPassword())){
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }
            PrincipalAdminDetails adminDetails = new PrincipalAdminDetails(admin.get());
            accessToken = jwtTokenProvider.generateToken(adminDetails, false);
            refreshToken = jwtTokenProvider.generateToken(adminDetails, true);
        }

        try {

            HttpHeaders headers = createTokenCookies(accessToken, refreshToken);
            headers.add("Location", "http://localhost:3000/login/callback");

            logArchiveLoginRepository.save(LogArchiveLoginModel.builder()
                    .userId(user.orElse(null))
                    .adminId(admin.orElse(null)).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Messenger.builder()
                            .message("Login Successful")
                            .build());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Messenger.builder()
                                    .message("Invalid User")
                                    .build());
        }
    }

    @Override
    public ResponseEntity<Messenger> refresh(HttpServletRequest request) {
        try {
            String jwtToken = jwtTokenProvider.getCookieValue(request, "refreshToken");
            //유효성 검사
            if (jwtToken == null) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Refresh token is missing");
            }

            if (!jwtTokenProvider.isTokenValid(jwtToken, true)) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Invalid Refresh Token");
            }

            //사용자 정보 추출
            Object accountDetails = jwtTokenProvider.extractPrincipalDetails(jwtToken);
            //db에 token 존재 여부
            boolean isTokenExists;
            if (accountDetails instanceof PrincipalUserDetails) {
                isTokenExists = jwtTokenProvider.isTokenExists(((PrincipalUserDetails) accountDetails).getUsername(), jwtToken);
            } else if (accountDetails instanceof PrincipalAdminDetails) {
                isTokenExists = jwtTokenProvider.isTokenExists(((PrincipalAdminDetails) accountDetails).getUsername(), jwtToken);
            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Token not found in DB");
            }
            if (!isTokenExists) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Token not found in DB");
            }

            // 새로운 Access Token 발행
            String accessToken = jwtTokenProvider.generateToken(accountDetails, false);

            HttpHeaders headers = createSingleTokenCookie("accessToken", accessToken, jwtTokenProvider.getAccessTokenExpired());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Messenger.builder()
                            .message("Access Token refreshed successfully")
                            .build());

        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());

        } catch (Exception e) {
            log.error("Failed to refresh token", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Messenger.builder()
                            .message("Failed to refresh token")
                            .build());
        }
    }

    @Override
    public ResponseEntity<Messenger> logout(HttpServletRequest request) {
        try {
            String jwtToken = jwtTokenProvider.getCookieValue(request, "accessToken");
            //유효성 검사
            if (jwtToken == null) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Access token is missing");
            }
            //유효성 검사
            if (!jwtTokenProvider.isTokenValid(jwtToken, false)) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Invalid Refresh Token");
            }

            Object accountDetails = jwtTokenProvider.extractPrincipalDetails(jwtToken);
            log.info("accountDetails : {}", accountDetails);

            boolean isRemoved;
            if (accountDetails instanceof PrincipalUserDetails) {
                isRemoved = jwtTokenProvider.invalidateToken(((PrincipalUserDetails) accountDetails).getUsername());
            } else if (accountDetails instanceof PrincipalAdminDetails) {
                isRemoved = jwtTokenProvider.invalidateToken(((PrincipalAdminDetails) accountDetails).getUsername());
            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Invalid User Type");
            }

            // if token not found in Redis, throw error
            if (!isRemoved) {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Token not found in DB");
            }

            // Clear the tokens from the cookies by setting them with maxAge 0
            HttpHeaders headers = clearTokenCookies();

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(Messenger.builder()
                            .message("Logout Successful")
                            .build());

        } catch (GlobalException e) {
            return ResponseEntity.status(e.getStatus().getHttpStatus())
                    .body(Messenger.builder()
                            .message(e.getMessage())
                            .build());
        } catch (Exception e) {
            log.error("Failed to logout", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Messenger.builder()
                            .message("Failed to logout")
                            .build());
        }
    }

    @Override
    public ResponseEntity<Messenger> validatePasswordSetupToken(TokenValidationRequestDto request) {
        if (passwordTokenProvider.isPasswordSetupTokenValid(request.getToken())) {
            return ResponseEntity.ok(Messenger.builder()
                    .message("Password setup token is valid").build());
        } else {
            return ResponseEntity.status(ExceptionStatus.UNAUTHORIZED.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Invalid or expired token")
                            .build());
        }
    }

    @Override
    @Transactional
    public ResponseEntity<Messenger> setupPassword(PasswordSetupRequestDto request) {
        String token = request.getToken();

        if (!passwordTokenProvider.isPasswordSetupTokenValid(token)) {
            return ResponseEntity.status(ExceptionStatus.UNAUTHORIZED.getHttpStatus()).body(Messenger.builder()
                    .message("Invalid or expired token").build());
        }

        // 비밀번호 강도 검사
        if (!isValidPassword(request.getNewPassword())) {
            return ResponseEntity.status(ExceptionStatus.INVALID_INPUT.getHttpStatus()).body(Messenger.builder()
                    .message("Password does not meet the security requirements.").build());
        }
        String employeeId = passwordTokenProvider.extractEmployeeId(request.getToken());
        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            return ResponseEntity.badRequest().body(Messenger.builder()
                    .message("Passwords do not match").build());
        }

        try {
            String newPassword = request.getNewPassword();
            Optional<UserTopModel> user = userTopRepository.findByEmployeeId(employeeId);

            if (user.isPresent()) {
                // 사용자 비밀번호 업데이트
                user.get().setPassword(passwordEncoder.encode(newPassword));
                userTopRepository.save(user.get());
            } else {
                // 관리자 비밀번호 업데이트
                Optional<AdminTopModel> admin = adminTopRepository.findByEmployeeId(employeeId);

                if (admin.isEmpty()) {
                    return ResponseEntity.status(ExceptionStatus.USER_NOT_FOUND.getHttpStatus())
                            .body(Messenger.builder()
                                    .message("User not found with employee ID: " + employeeId).build());
                }

                admin.get().setPassword(passwordEncoder.encode(newPassword));
                adminTopRepository.save(admin.get());
            }

            // 토큰 무효화 처리 (필요 시)
            passwordTokenProvider.invalidateToken(token);

            return ResponseEntity.ok().body(Messenger.builder()
                    .message("Password has been set successfully")
                    .build());

        } catch (Exception e) {
            log.error("Failed to set password for employee ID : {} ", employeeId, e);
            return ResponseEntity.status(ExceptionStatus.INTERNAL_SERVER_ERROR.getHttpStatus())
                    .body(Messenger.builder()
                            .message("Failed to set password due to an internal error").build());
        }
    }

    private HttpHeaders createTokenCookies(String accessToken, String refreshToken) {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", accessToken)
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpired())
//                    .httpOnly(true)
//                    .secure(true)  // Enable for HTTPS
//                    .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpired())
//                    .httpOnly(true)
//                    .secure(true)  // Enable for HTTPS
//                    .sameSite("Lax")
                .build();
        HttpHeaders headers = new HttpHeaders();

        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());

        return headers;
    }

    private HttpHeaders createSingleTokenCookie(String cookieName, String token, Long maxAge) {

        ResponseCookie tokenCookie = ResponseCookie.from(cookieName, token)
                .path("/")
                .maxAge(maxAge)
                .httpOnly(true)
                .secure(true)  // Enable for HTTPS in production
                .sameSite("Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, tokenCookie.toString());
        return headers;
    }

    private HttpHeaders clearTokenCookies() {
        ResponseCookie accessTokenCookie = ResponseCookie.from("accessToken", "")
                .path("/")
                .maxAge(0L)
//                .httpOnly(true)
//                .secure(true)
//                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0L) // Set max age to 0 to delete the cookie
//                .httpOnly(true)
//                .secure(true)  // Enable for HTTPS in production
//                .sameSite("Lax")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, accessTokenCookie.toString());
        headers.add(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString());
        return headers;
    }

    private boolean isValidPassword(String password) {
        String passwordPattern = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=]).{8,}$";
        return password.matches(passwordPattern);
    }


}

