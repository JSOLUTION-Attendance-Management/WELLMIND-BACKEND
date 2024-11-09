package site.wellmind.security.interceptor;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import site.wellmind.security.provider.JwtTokenProvider;

/**
 * AuthenticationInterceptor
 * <p>Checks user authentication and authorization with access and refresh tokens.</p>
 * <p>If the access token is expired but refresh token is valid, prompts the client to refresh the token.</p>
 * @see JwtTokenProvider
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {
    private final JwtTokenProvider jwtTokenProvider;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String accessToken = getTokenFromCookie(request, "accessToken");
        String refreshToken = getTokenFromCookie(request, "refreshToken");

        if(accessToken!=null && jwtTokenProvider.isTokenValid(accessToken,false)){
            setAuthenticaton(accessToken);
        }
        return true;
    }

    private void setAuthentication(String token){
        Authentication authentication=jwtTokenProvider.get
    }

    private String getTokenFromCookie(HttpServletRequest request,String cookieName){
       if(request.getCookies()!=null){
           for(Cookie cookie : request.getCookies()){
               if(cookieName.equals(cookie.getName())){
                   return cookie.getValue();
               }
           }
       }
        return null;
    }

}
