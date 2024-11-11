package site.wellmind.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import site.wellmind.common.domain.dto.Messenger;

import java.io.IOException;

/**
 * CustomAuthenticationEntryPoint
 * <p>Handles access denied exceptions for authorized users attempting unauthorized access.</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j(topic = "UNAUTHORIZATION_EXCEPTION_HANDLER")
@Component
@RequiredArgsConstructor
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        log.warn("Access denied for user attempting to access {}: {}", request.getRequestURI(), accessDeniedException.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Messenger messenger = Messenger.builder()
                .message("Access denied - you do not have permission to access this resource.")
                .build();

        response.getOutputStream().write(objectMapper.writeValueAsBytes(messenger));
    }
}
