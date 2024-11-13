package site.wellmind.common.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.web.client.RestTemplate;
import site.wellmind.common.service.CommandService;
import site.wellmind.common.service.QueryService;
import site.wellmind.user.domain.dto.UserDto;

/**
 * AppConfig
 * <p>공통 설정을 관리하는 Config 클래스</p>
 * @since 2024-11-08
 * @version 1.0
 */

@Configuration
@EnableAspectJAutoProxy //AOP 지원을 활성화
public class AppConfig {
    @Bean
    public RestTemplate restTemplate(){
        return new RestTemplate();
    }
}
