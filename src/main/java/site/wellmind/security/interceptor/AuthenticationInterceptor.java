package site.wellmind.security.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.user.domain.dto.AccountDto;

import java.util.stream.Collectors;

/**
 * AuthenticationInterceptor
 * <p>Checks user authentication and authorization with access and refresh tokens.</p>
 * <p>If the access token is expired but refresh token is valid, prompts the client to refresh the token.</p>
 * @see JwtTokenProvider
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j(topic = "AuthenticationInterceptor")
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = jwtTokenProvider.getCookieValue(request, "accessToken");

        if (accessToken == null || accessToken.isEmpty()) {
            log.warn("Access token is missing.");
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED, "Access token is missing.");
        }

        Long accountId = jwtTokenProvider.extractId(accessToken);
        String employeeId = jwtTokenProvider.extractEmployeeId(accessToken);
        String role = jwtTokenProvider.extractRoles(accessToken).toString();
        role = role.replaceAll("[\\[\\]]", "");

        boolean isAdmin = SecurityContextHolder.getContext().getAuthentication()
                .getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().startsWith("ROLE_ADMIN"));

        request.setAttribute("accountDto", AccountDto.builder()
                .accountId(accountId)
                .employeeId(employeeId)
                .role(role)
                .isAdmin(isAdmin)
                .build());

        return true;
    }

}
