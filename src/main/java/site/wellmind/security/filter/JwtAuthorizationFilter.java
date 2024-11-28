package site.wellmind.security.filter;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import site.wellmind.common.domain.vo.ExceptionStatus;
import site.wellmind.common.domain.vo.Role;
import site.wellmind.common.exception.GlobalException;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.util.RoleManager;
import site.wellmind.user.repository.AccountRoleRepository;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * JwtAuthorizationFilter
 * <p>Checks user authentication and authorization with access and refresh tokens.</p>
 * <p>If the access token is expired but refresh token is valid, prompts the client to refresh the token.</p>
 * @see JwtTokenProvider
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @since 2024-11-09
 */
@Slf4j(topic = "JwtAuthorizationFilter")
@Component
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleManager roleManager;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();
    //인증에서 제외할 url
    private static final List<String> AUTH_WHITELIST=Arrays.asList(
            "/api/public/**",
            "/api/v1/member/**", "/swagger-ui/**", "/api-docs", "/swagger-ui.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/api/v1/auth/**",
            "/swagger-ui/index.html"
    );
    private static final List<String> AUTH_BLACKLIST=Arrays.asList(
            "/api/public/logout/**",
            "/api/public/modify-by-password/**"
    );


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, jakarta.servlet.FilterChain filterChain) throws jakarta.servlet.ServletException, IOException {
        String path=request.getRequestURI();
        log.info("Request URI: {}", path);

        if (AUTH_BLACKLIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            log.info("AUTH_BLACKLIST - Authentication required for this path");
        } else if (AUTH_WHITELIST.stream().anyMatch(pattern -> pathMatcher.match(pattern, path))) {
            log.info("AUTH_WHITELIST");
            filterChain.doFilter(request,response);
            return;
        }

        try{
            log.info("try start : {}",path);
            String accessToken=jwtTokenProvider.getCookieValue(request,"accessToken");
            log.info("accessToken : {}",accessToken);
            if(accessToken==null){
                handleTokenRefresh(request,response);
                return;
            }
            //if access token is valid
            //Check if user has required roles
            List<String> accountRoles=jwtTokenProvider.extractRoles(accessToken).stream()
                    .toList();
            boolean hasRole=accountRoles.stream().anyMatch(roleManager.getRoleIds()::contains);

            log.info("accountRoles: {} ",accountRoles);
            log.info("accountId: {} ",jwtTokenProvider.extractEmployeeId(accessToken));

            if(!hasRole){
                throw new GlobalException(ExceptionStatus.NO_PERMISSION,"No permission");
            }

            Authentication authentication= jwtTokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);

             //사용자가 모든 검사를 통과한 경우(즉, 가 유효하고, 가 refreshToken필요하지 않으며, 사용자에게 필요한 역할이 있는 경우) 이 줄은 필터 체인을 통해 요청의 여정을 계속
            filterChain.doFilter(request,response);
        }catch (GlobalException e){
            onError((HttpServletResponse) response,e.getStatus().getErrorCode(),e.getMessage());
        }
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
