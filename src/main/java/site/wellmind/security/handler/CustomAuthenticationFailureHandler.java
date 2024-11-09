package site.wellmind.security.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import site.wellmind.security.provider.JwtTokenProvider;

import java.io.IOException;

/**
 * CustomAuthenticationFailureHandler
 * <p>Handles actions to be taken on authentication failure</p>
 * <p>Redirects to a specified URL and provides a JSON response with error details</p>
 *
 * @since 2024-11-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        log.error("Authentication failure: {}", exception.getMessage());

        response.setStatus(HttpStatus.FOUND.value());
        response.setHeader("Location","http://localhost:3000/login/callback");  //리다이렉션 설정
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(String.format("{\"status\": %d, \"error\": \"Authentication Failed\", \"message\": \"%s\"}",
                HttpStatus.UNAUTHORIZED.value(),
                exception.getMessage()));

    }
}
