package site.wellmind.security.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import site.wellmind.common.domain.dto.Messenger;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.provider.JwtTokenProvider;

import java.io.IOException;

/**
 * CustomAuthenticationSuccessHandler
 * <p>Authentication success handler that handles token generation and cookie setup</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see JwtTokenProvider
 * @see PrincipalUserDetails
 * @see PrincipalAdminDetails
 * @since 2024-11-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    private final ObjectMapper objectMapper;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try{
            log.info("::::::Request 정보: " + request);
            log.info("::::::Authentication 정보: " + authentication);
            log.info("::::::Authorities 정보: " + authentication.getAuthorities());
            log.info("::::::Credentials 정보: " + authentication.getCredentials());

            String accessToken;
            String refreshToken;

            if(authentication.getPrincipal() instanceof PrincipalUserDetails){
                PrincipalUserDetails userDetails = (PrincipalUserDetails) authentication.getPrincipal();
                accessToken = jwtTokenProvider.generateToken(userDetails, false);
                refreshToken = jwtTokenProvider.generateToken(userDetails, true);
            }else if(authentication.getPrincipal() instanceof PrincipalAdminDetails){
                PrincipalAdminDetails adminDetails = (PrincipalAdminDetails) authentication.getPrincipal();
                accessToken = jwtTokenProvider.generateToken(adminDetails, false);
                refreshToken = jwtTokenProvider.generateToken(adminDetails, true);
            }else{
                log.error("Invalid user type during authentication");
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.getOutputStream().write(writeValueAsBytes(Messenger.builder()
                        .message("Invalid User Type").build()));
                return;
            }

            ResponseCookie accessTokenCookie=ResponseCookie.from("accessToken",accessToken)
                    .path("/")
                    .maxAge(jwtTokenProvider.getAccessTokenExpired())
//                    .httpOnly(true)
//                    .secure(true)  // Enable for HTTPS
//                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie",accessTokenCookie.toString());

            ResponseCookie refreshTokenCookie=ResponseCookie.from("refreshToken",refreshToken)
                    .path("/")
                    .maxAge(jwtTokenProvider.getRefreshTokenExpired())
//                    .httpOnly(true)
//                    .secure(true)  // Enable for HTTPS
//                    .sameSite("Lax")
                    .build();
            response.addHeader("Set-Cookie",refreshTokenCookie.toString());

            // set response status and redirect URL
            response.setStatus(HttpStatus.FOUND.value());
            response.setHeader("Location","http://localhost:3000/login/callback");
            response.setContentType("application/json");

            // Create and send success message
            Messenger messenger = Messenger.builder().message("Login Successful").build();
            response.getOutputStream().write(writeValueAsBytes(messenger));

        }catch (IOException e){
            log.error("Error writing response data during authentication success",e);
            response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            try{
                response.getOutputStream().write(writeValueAsBytes(Messenger.builder()
                        .message("Internal Server Error").build()));
            }catch (IOException ex){
                log.error("Failed to send error response",ex);
            }
        }
    }

    private byte[] writeValueAsBytes(Messenger messenger){
        try{
            return objectMapper.writeValueAsBytes(messenger);  //객체를 JSON 형식의 바이트 배열로 직렬화
        }catch (JsonProcessingException e){
            throw new RuntimeException("JSON serialization error", e);
        }
    }
}
