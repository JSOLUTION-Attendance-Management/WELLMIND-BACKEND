package site.wellmind.security.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import site.wellmind.security.filter.JwtAuthorizationFilter;
import site.wellmind.security.handler.CustomAccessDeniedHandler;
import site.wellmind.security.handler.CustomAuthenticationEntryPoint;
import site.wellmind.security.handler.CustomAuthenticationFailureHandler;
import site.wellmind.security.handler.CustomAuthenticationSuccessHandler;
import site.wellmind.security.provider.JwtTokenProvider;
import site.wellmind.security.util.RoleManager;

import java.util.List;

/**
 * WebSecurityConfig
 * <p>Spring Security의 모든 보안 설정을 구성</p>
 *
 * @author Yuri Seok(tjrdbfl)
 * @version 1.0
 * @see CustomAuthenticationSuccessHandler
 * @see CustomAuthenticationFailureHandler
 * @since 2024-11-02
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) // Replaces EnableGlobalMethodSecurity
public class WebSecurityConfig {
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
    private final JwtTokenProvider jwtTokenProvider;
    private final RoleManager roleManager;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private static final String[] AUTH_BLACKLIST={
          "/api/auth"
    };
    private static final String[] AUTH_WHITELIST = {
            "/", "/home",
            "/api/v1/member/**", "/swagger-ui/**", "/api-docs", "/swagger-ui.html",
            "/v3/api-docs/**", "/api-docs/**", "/swagger-ui.html", "/api/v1/auth/**"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .formLogin(form -> form.disable())
                .httpBasic(AbstractHttpConfigurer::disable)  //jwt 사용으로 HTTP 기본 인증 필요 x
                .addFilterBefore(new JwtAuthorizationFilter(jwtTokenProvider,roleManager), UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling((exceptionHandling) ->
                        exceptionHandling
                                .authenticationEntryPoint(customAuthenticationEntryPoint)
                                .accessDeniedHandler(customAccessDeniedHandler))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(AUTH_BLACKLIST).authenticated()
                        .requestMatchers(AUTH_WHITELIST).permitAll()
                        .anyRequest().permitAll()
                )
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );
        //.logout(logout -> logout.permitAll());
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("http://localhost:3000", "https://www.wellmind.site"));  //추후 변경 필요
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    //PasswordEncoder : 인증을 위해 비밀번호를 암호화하고 비교하는 역할
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
