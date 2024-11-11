package site.wellmind.user.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.domain.dto.LoginDto;
import site.wellmind.user.domain.model.AdminTopModel;
import site.wellmind.user.domain.model.UserTopModel;
import site.wellmind.user.repository.AdminTopRepository;
import site.wellmind.user.repository.UserTopRepository;
import site.wellmind.user.service.AuthService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final RestTemplate restTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserTopRepository userTopRepository;
    private final AdminTopRepository adminTopRepository;
    private final PasswordEncoder passwordEncoder;
    @Override
    @Transactional
    public ResponseEntity<Messenger> localLogin(LoginDto dto) {
        String employeeId=dto.getEmployeeId();
        String password= dto.getPassword();;
        Optional<UserTopModel> user=userTopRepository.findByEmployeeId(employeeId);
        Optional<AdminTopModel> admin=adminTopRepository.findByEmployeeId(employeeId);

        if(user.isEmpty() && admin.isEmpty()){
            throw new GlobalException(ExceptionStatus.USER_NOT_FOUND,ExceptionStatus.USER_NOT_FOUND.getMessage());
        }else if(user.isPresent()){
            if(!passwordEncoder.matches(password,user.get().getPassword())){
                throw new GlobalException(ExceptionStatus.INVALID_CREDENTIALS,ExceptionStatus.INVALID_CREDENTIALS.getMessage());
            }
        } else if (admin.isPresent()) {
            if(!passwordEncoder.matches(password,admin.get().getPassword())){
                throw new GlobalException(ExceptionStatus.INVALID_CREDENTIALS,ExceptionStatus.INVALID_CREDENTIALS.getMessage());
            }
        }

        try{
            //요청 보내기
            Object accountDetails = restTemplate.postForObject(
                    "http://localhost:8080/auth/login",
                    dto,
                    Object.class
            );

            if(accountDetails==null){
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid User");
            }

            String accessToken;
            String refreshToken;
            if (accountDetails instanceof PrincipalUserDetails) {
                accessToken = jwtTokenProvider.generateToken((PrincipalUserDetails) accountDetails, false);
                refreshToken = jwtTokenProvider.generateToken((PrincipalUserDetails) accountDetails, true);
            } else if (accountDetails instanceof PrincipalAdminDetails) {
                accessToken = jwtTokenProvider.generateToken((PrincipalAdminDetails) accountDetails, false);
                refreshToken = jwtTokenProvider.generateToken((PrincipalAdminDetails) accountDetails, true);
            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Invalid User Type");
            }


            HttpHeaders headers = createTokenCookies(accessToken, refreshToken);
            headers.add("Location", "http://localhost:3000/login/callback");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(Messenger.builder()
                            .message("Login Successful")
                            .build());

        }catch(Exception e){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(
                    Messenger.builder()
                            .message("Invalid User")
                            .build());
        }
    }

    @Override
    public ResponseEntity<Messenger> refresh(String refreshToken) {
        try{
            //Bearer 토큰 제거
            String jwtToken=jwtTokenProvider.removeBearer(refreshToken);
            //유효성 검사
            if(!jwtTokenProvider.isTokenValid(jwtToken,true)){
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid Refresh Token");
            }

            //사용자 정보 추출
            Object accountDetails=jwtTokenProvider.extractPrincipalDetails(jwtToken);
            //db에 token 존재 여부
            boolean isTokenExists;
            if (accountDetails instanceof PrincipalUserDetails){
                isTokenExists= jwtTokenProvider.isTokenExists( ((PrincipalUserDetails) accountDetails).getUsername(),jwtToken);
            }else if (accountDetails instanceof PrincipalAdminDetails) {
                isTokenExists= jwtTokenProvider.isTokenExists( ((PrincipalAdminDetails) accountDetails).getUsername(),jwtToken);
            } else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Token not found in DB");
            }

            // 새로운 Access Token 발행
            String accessToken=jwtTokenProvider.generateToken(accountDetails,false);

            // ResponseCookie 로 쿠키 설정
            ResponseCookie accessTokenCookie=ResponseCookie.from("accessToken",accessToken)
                    .maxAge(jwtTokenProvider.getAccessTokenExpired())
                    .path("/")
                    .domain("")
                    .build();

            return ResponseEntity.ok()
                    .header("Set-Cookie",accessTokenCookie.toString())
                    .body(Messenger.builder()
                            .message("Access Token refreshed successfully")
                            .build());
        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Messenger.builder()
                            .message("Failed to refresh token")
                            .build());
        }
    }

    @Override
    public ResponseEntity<Messenger> logout(String refreshToken) {
        try{
            //Bearer 토큰 제거
            String jwtToken=jwtTokenProvider.removeBearer(refreshToken);
            //유효성 검사
            if(!jwtTokenProvider.isTokenValid(jwtToken,true)){
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid Refresh Token");
            }

            Object accountDetails=jwtTokenProvider.extractPrincipalDetails(jwtToken);

            boolean isRemoved;
            if (accountDetails instanceof PrincipalUserDetails){
                isRemoved= jwtTokenProvider.invalidateToken(((PrincipalUserDetails) accountDetails).getUsername());
            }else if (accountDetails instanceof PrincipalAdminDetails){
                isRemoved= jwtTokenProvider.invalidateToken(((PrincipalAdminDetails) accountDetails).getUsername());
            }else {
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid User Type");
            }

            // if token not found in Redis, throw error
            if(!isRemoved){
                throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Token not found in DB");
            }

            return ResponseEntity.ok(Messenger.builder()
                    .message("Logout Successful").build());

        }catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Messenger.builder()
                            .message("Failed to logout")
                            .build());
        }
    }

    private HttpHeaders createTokenCookies(String accessToken,String refreshToken){
        ResponseCookie accessTokenCookie=ResponseCookie.from("accessToken",accessToken)
                .path("/")
                .maxAge(jwtTokenProvider.getAccessTokenExpired())
//                    .httpOnly(true)
//                    .secure(true)  // Enable for HTTPS
//                    .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie=ResponseCookie.from("refreshToken",refreshToken)
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
}
