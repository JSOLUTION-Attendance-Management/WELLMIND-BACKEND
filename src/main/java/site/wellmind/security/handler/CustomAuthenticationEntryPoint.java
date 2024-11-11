package site.wellmind.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import site.wellmind.common.domain.dto.Messenger;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint
 * <p>Handles unauthorized access attempts to protected resources by responding with 401 status and JSON message.</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j(topic = "UNAUTHORIZATION_EXCEPTION_HANDLER")
@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        log.warn("Unauthorized access attempt to {} with message: {}", request.getRequestURI(), authException.getMessage());

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Messenger messenger = Messenger.builder()
                .message("Unauthorized access - please log in.")
                .build();

        response.getOutputStream().write(objectMapper.writeValueAsBytes(messenger));
    }
}
