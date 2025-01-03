package site.wellmind.common.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import site.wellmind.common.interceptor.RequestResponseInterceptor;
import site.wellmind.security.interceptor.AuthorizationInterceptor;

/**
 * WebConfig
 * <p>RequestResponseInterceptor, AuthorizationInterceptor 등록을 위한 config 클래스</p>
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see RequestResponseInterceptor
 * @see AuthorizationInterceptor
 * @since 2024-11-09
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final RequestResponseInterceptor requestResponseInterceptor;
    private final AuthorizationInterceptor authenticationInterceptor;
    @Override
    public void addInterceptors(InterceptorRegistry registry){
        registry.addInterceptor(requestResponseInterceptor).addPathPatterns("/**");
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/auth/**")
                .addPathPatterns("/api/log/**")
                .addPathPatterns("/api/transfer/**")
                .addPathPatterns("/api/qr/**")
                .addPathPatterns("/api/report/**")
                .addPathPatterns("/api/public/modify-by-password")
                .addPathPatterns("/api/attend/**");

    }

}
