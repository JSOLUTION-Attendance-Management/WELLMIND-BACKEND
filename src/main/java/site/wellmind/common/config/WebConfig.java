package site.wellmind.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.wellmind.common.interceptor.RequestResponseInterceptor;
import site.wellmind.security.domain.model.PrincipalAdminDetails;
import site.wellmind.security.domain.model.PrincipalUserDetails;
import site.wellmind.security.interceptor.AuthenticationInterceptor;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.resolver.CurrentAccountIdResolver;

import java.util.List;

/**
 * WebConfig
 * <p>RequestResponseInterceptor, AuthenticationInterceptor 등록을 위한 config 클래스</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see RequestResponseInterceptor
 * @see AuthenticationInterceptor
 * @see CurrentAccountIdResolver
 * @since 2024-11-09
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestResponseInterceptor requestResponseInterceptor;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final CurrentAccountIdResolver currentAccountIdResolver;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(requestResponseInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor).addPathPatterns("/secure/**");
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(currentAccountIdResolver);
    }

}
