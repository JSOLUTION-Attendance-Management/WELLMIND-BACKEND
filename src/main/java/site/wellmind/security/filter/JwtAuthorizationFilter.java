package site.wellmind.security.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.provider.JwtTokenProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JwtAuthorizationFilter
 * <p>Checks user authentication and authorization with access and refresh tokens.</p>
 * <p>If the access token is expired but refresh token is valid, prompts the client to refresh the token.</p>
 * @see JwtTokenProvider
 * @see Role
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j
@Component
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    @Setter
    private List<Role> roles;

    //인증에서 제외할 url
    private static final List<String> AUTH_WHITELIST=Arrays.asList(
            "/", "/home","/api/auth/**",
            "/api/v1/member/**", "/swagger-ui/**", "/api-docs", "/swagger-ui.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/api/v1/auth/**"
    );

    public JwtAuthorizationFilter(JwtTokenProvider jwtTokenProvider){
        this.jwtTokenProvider=jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String path=request.getRequestURI();
        if(AUTH_WHITELIST.stream().anyMatch(path::startsWith)){
            filterChain.doFilter(request,response);
            return;
        }
        try{
            String accessToken=jwtTokenProvider.getCookieValue(request,"accessToken");

            //if access token isn't valid
            if(accessToken==null){
                handleTokenRefresh(request,response);
                return;
            }

            //if access token is valid
            //Check if user has required roles
            List<Role> accountRoles=jwtTokenProvider.extractRoles(accessToken).stream()
                    .map(Role::valueOf)
                    .toList();

            boolean hasRole=accountRoles.stream().anyMatch(roles::contains);
            if(!hasRole){
                throw new GlobalException(ExceptionStatus.NO_PERMISSION,"No permission");
            }

            //set authentication in security context if token is valid
            UsernamePasswordAuthenticationToken authenticationToken=new UsernamePasswordAuthenticationToken(jwtTokenProvider.extractPrincipalDetails(accessToken),null);
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            //사용자가 모든 검사를 통과한 경우(즉, 가 유효하고, 가 refreshToken필요하지 않으며, 사용자에게 필요한 역할이 있는 경우) 이 줄은 필터 체인을 통해 요청의 여정을 계속
            filterChain.doFilter(request,response);
        }catch (GlobalException e){
            onError((HttpServletResponse) response,e.getStatus().getErrorCode(),e.getMessage());
        }
    }

    @Data
    public static class Config{
        private List<Role> roles;
    }
    
    @Override
    public void destroy() {
        log.info("JwtAuthorizationFilter destroyed.");
        //closing database connections or stopping threads
    }

    private void handleTokenRefresh(HttpServletRequest request,HttpServletResponse response) throws IOException{
        //get refresh token
        String refreshToken=jwtTokenProvider.getCookieValue((HttpServletRequest) request,"refreshToken");
        //if refresh token is null
        if(refreshToken==null || !jwtTokenProvider.isTokenValid(refreshToken,true)){
            throw new GlobalException(ExceptionStatus.UNAUTHORIZED,"Invalid Tokens");
        }else{
            // Prompt client to refresh the access token
            ((HttpServletResponse) response).setHeader("X-Token-Status", "ExpiredAccessToken");
            ((HttpServletResponse) response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            ((HttpServletResponse) response).getWriter().write("Access token expired. Please refresh the token.");
        }
    }

    private void onError(HttpServletResponse response,int statusCode,String message) throws IOException{
        log.error("Error Occurred: Status Code = {}, Message = {}", statusCode, message);
        response.setStatus(statusCode);
        response.getWriter().write(message);
    }

}