package site.wellmind.common.interceptor;

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

/**
 * RequestResponseInterceptor
 * <p>일반적인 요청 및 응답 세부 정보 로깅을 위한 인터셉터</p>
 * <p>요청을 처리하는 데 걸리는 시간, 사용자 세부 정보 기록</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RequestResponseInterceptor implements HandlerInterceptor {
    private static final String START_TIME_ATTRIBUTE = "startTime";
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long startTime=System.currentTimeMillis();
        request.setAttribute(START_TIME_ATTRIBUTE,startTime);
        log.info("Request URI: {}", request.getRequestURI());
        log.info("HTTP Method: {}", request.getMethod());
        log.info("Client IP: {}", request.getRemoteAddr());
        log.info("User-Agent: {}", request.getHeader("User-Agent"));
        log.info("Referer: {}", request.getHeader("Referer"));

        Authentication authentication= SecurityContextHolder.getContext().getAuthentication();
        if(authentication!=null && authentication.isAuthenticated()){
            log.info("Authenticated User : {}, Roles : {}",
                    authentication.getName(),
                    authentication.getAuthorities());
        }

        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable ModelAndView modelAndView) throws Exception {
        log.info("Response Status: {}", response.getStatus());
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, @Nullable Exception ex) throws Exception {
        long startTime=(Long) request.getAttribute(START_TIME_ATTRIBUTE);
        long endTime=System.currentTimeMillis();
        long executionTime=endTime-startTime;

        if (ex != null) {
            log.error("Exception: ", ex);
        }
        log.info("Request Completed in {} ms", executionTime);
    }
}
