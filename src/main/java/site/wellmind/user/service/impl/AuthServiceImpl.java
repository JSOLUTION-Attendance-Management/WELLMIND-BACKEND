package site.wellmind.user.service.impl;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.dto.TokenValidationRequestDto;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.common.service.CoolSmsService;
import site.wellmind.common.service.UtilService;
import site.wellmind.log.domain.model.LogArchiveLoginModel;
import site.wellmind.log.repository.LogArchiveLoginRepository;
import site.wellmind.security.domain.model.*;
import site.wellmind.security.domain.vo.RequestStatus;
import site.wellmind.security.domain.vo.TokenStatus;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.domain.dto.LoginDto;
import site.wellmind.security.provider.PasswordTokenProvider;
import site.wellmind.security.repository.AccountTokenRepository;
import site.wellmind.security.repository.SmsVerificationRepository;
import site.wellmind.security.util.EncryptionUtil;
import site.wellmind.user.domain.dto.*;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.AuthService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private final SmsVerificationRepository smsVerificationRepository;

    private final UtilService utilService;
    private final CoolSmsService coolSmsService;
    private final EncryptionUtil encryptionUtil;
    //private final TwilioConfig twilioConfig;

    @Override
    @Transactional
    public ResponseEntity<Messenger> localLogin(LoginDto dto) {

        String encoder = passwordEncoder.encode(dto.getPassword());

        String employeeId = dto.getEmployeeId();
        String password = dto.getPassword();

        log.info("encoder : {}", encoder);
        log.info("encoder : {}", passwordEncoder.matches(password, encoder));

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

            //임시 발급 비밀번호가 만료되었을 때
            if (user.get().getPasswordExpiry() != null && user.get().getPasswordExpiry().isAfter(LocalDateTime.now())) {
                userTopRepository.updatePasswordExpiry(user.get().getEmployeeId());
            }
            if (!passwordEncoder.matches(password, user.get().getPassword())) {
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }

            PrincipalUserDetails userDetails = new PrincipalUserDetails(user.get());
            accessToken = jwtTokenProvider.generateToken(userDetails, false);
            refreshToken = jwtTokenProvider.generateToken(userDetails, true);

        } else {

            if (admin.get().getPasswordExpiry() != null && admin.get().getPasswordExpiry().isBefore(LocalDateTime.now())) {
                adminTopRepository.updatePasswordExpiry(admin.get().getEmployeeId());
            }
            if (!passwordEncoder.matches(password, admin.get().getPassword())) {
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }

            PrincipalAdminDetails adminDetails = new PrincipalAdminDetails(admin.get());
            accessToken = jwtTokenProvider.generateToken(adminDetails, false);
            refreshToken = jwtTokenProvider.generateToken(adminDetails, true);
        }

        try {

            HttpHeaders headers = createTokenCookies(accessToken, refreshToken);
            headers.add("Location", "http://localhost:5731/login/callback");

            logArchiveLoginRepository.save(LogArchiveLoginModel.builder()
                    .userId(user.orElse(null))
                    .adminId(admin.orElse(null)).build());

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Messenger.builder()
                            .data(TestTokenDto.builder()
                                    .accessToken(accessToken)
                                    .refreshToken(refreshToken).build())
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
    public ResponseEntity<?> refresh(HttpServletRequest request) {
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
                    .body(TestTokenDto.builder()
                            .accessToken(accessToken).build()
                    );

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
    public ResponseEntity<Messenger> modifyByPassword(PasswordModifyRequestDto passwordDto, AccountDto accountDto) {
        String oldPassword = passwordDto.getOldPassword();
        String newPassword = passwordDto.getNewPassword();
        String confirmPassword = passwordDto.getConfirmNewPassword();

        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.status(ExceptionStatus.INVALID_INPUT.getHttpStatus())
                    .body(Messenger.builder()
                            .message("새 비밀번호와 비밀번호 확인 입력이 불일치합니다.")
                            .build());
        }


        if (accountDto.isAdmin()) {
            Optional<AdminTopModel> admin = adminTopRepository.findById(accountDto.getAccountId());
            log.info("admin : {}", admin);

            if (admin.isEmpty()) {
                return ResponseEntity.status(ExceptionStatus.ADMIN_NOT_FOUND.getHttpStatus())
                        .body(Messenger.builder()
                                .message(ExceptionStatus.ACCOUNT_NOT_FOUND.getMessage()).build());
            }
            if (!passwordEncoder.matches(oldPassword, admin.get().getPassword())) {
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }
            adminTopRepository.updatePasswordByEmployeeId(admin.get().getEmployeeId(), passwordEncoder.encode(newPassword), null);

        } else {
            Optional<UserTopModel> user = userTopRepository.findById(accountDto.getAccountId());
            if (user.isEmpty()) {
                return ResponseEntity.status(ExceptionStatus.USER_NOT_FOUND.getHttpStatus())
                        .body(Messenger.builder()
                                .message(ExceptionStatus.USER_NOT_FOUND.getMessage()).build());
            }
            if (!passwordEncoder.matches(oldPassword, user.get().getPassword())) {
                throw new GlobalException(ExceptionStatus.INVALID_PASSWORD);
            }

            userTopRepository.updatePasswordByEmployeeId(user.get().getEmployeeId(), passwordEncoder.encode(newPassword), null);
        }

        jwtTokenProvider.invalidateToken(accountDto.getEmployeeId());

        HttpHeaders headers = clearTokenCookies();

        return ResponseEntity.ok()
                .headers(headers)
                .body(Messenger.builder()
                        .message("Modify Password Successful")
                        .build());
    }

    @Override
    @Transactional
    public ResponseEntity<Messenger> startVerification(UserVerifyCodeRequestDto phone) {

        // 같은 유저가 다른 번호로 인증 번호를 요청했을 때 막기
        SmsVerificationModel smsVerificationModel = smsVerificationRepository.findFirstByPhoneNumOrderByRegDateDesc(phone.getPhoneNum());
        int requestCount = smsVerificationModel != null ? smsVerificationModel.getRequestCount() : 0;
        LocalDateTime lastRequestTime = smsVerificationModel != null ? smsVerificationModel.getLastRequestTime() : LocalDateTime.MIN;
        // 요청 제한 검증
        if (isRequestLimitExceeded(requestCount, lastRequestTime, 5, 3, Duration.ofMinutes(10))) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Messenger.builder()
                            .message("요청 제한 초과: 하루 최대 5회 및 10분 내 최대 3회 인증 요청만 가능합니다.")
                            .build());
        }

        try {
            // 새로운 인증 요청 생성
            coolSmsService.sendSms(phone.getPhoneNum());

            return ResponseEntity.ok(Messenger.builder()
                    .message("인증 번호 발송 성공")
                    .build());
        } catch (Exception e) {
            log.info("Exception : {}", e);
            throw new GlobalException(ExceptionStatus.BAD_REQUEST, "인증 번호 요청 실패");
        }
    }


    private boolean isRequestLimitExceeded(int requestCount, LocalDateTime lastRequestTime, int dailyLimit, int intervalLimit, Duration intervalDuration) {
        if (requestCount >= dailyLimit && lastRequestTime.toLocalDate().equals(LocalDate.now())) {
            return true;
        }
        if (requestCount >= intervalLimit && lastRequestTime.isAfter(LocalDateTime.now().minus(intervalDuration))) {
            return true;
        }
        return false;
    }


    @Override
    public ResponseEntity<Messenger> checkVerification(UserVerifyCheckRequestDto userVerifyCheckRequestDto) {
        try {

            SmsVerificationModel smsVerificationModel = smsVerificationRepository.findFirstByPhoneNumOrderByRegDateDesc(userVerifyCheckRequestDto.getPhoneNum());

            if(encryptionUtil.decrypt(smsVerificationModel.getVerifyKey()).equals(userVerifyCheckRequestDto.getCode())){
                smsVerificationModel.setVerification(RequestStatus.Y);
                smsVerificationRepository.save(smsVerificationModel);
                return ResponseEntity.ok(Messenger.builder()
                        .message("인증 번호 검증 성공")
                        .build());
            }else{
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body(Messenger.builder()
                                .message("잘못된 인증 코드입니다.")
                                .build());
            }

        } catch (Exception e) {
            throw new GlobalException(ExceptionStatus.BAD_REQUEST, "인증 번호 검증 실패");
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
                    .httpOnly(false)
                    .secure(false)  // Enable for HTTPS
                    .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/")
                .maxAge(jwtTokenProvider.getRefreshTokenExpired())
                    .httpOnly(false)
                    .secure(false)  // Enable for HTTPS
                    .sameSite("Lax")
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
                .httpOnly(false)
                .secure(false)  // Enable for HTTPS in production
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
                .httpOnly(false)
                .secure(false)
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refreshToken", "")
                .path("/")
                .maxAge(0L) // Set max age to 0 to delete the cookie
                .httpOnly(false)
                .secure(false)  // Enable for HTTPS in production
                .sameSite("Lax")
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

