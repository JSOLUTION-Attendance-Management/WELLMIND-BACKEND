package site.wellmind.common.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI(){
        String jwt="JWT";

//        // Basic Auth 방식
//        return new OpenAPI()
//                .components(new Components()
//                        .addSecuritySchemes("basicAuth", new SecurityScheme()
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("basic")))
//                .info(apiInfo())
//                .addSecurityItem(new SecurityRequirement().addList("basicAuth"));
        // jwt 기반 인증 방식
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo())
                .addSecurityItem(new SecurityRequirement().addList(jwt))
                .components(new Components().addSecuritySchemes(jwt,new SecurityScheme()
                        .name(jwt)
                        .type(SecurityScheme.Type.HTTP)
                        .scheme("bearer")
                        .bearerFormat("JWT"))
                );
    }

    private Info apiInfo(){
        return new Info()
                .title("JSolution WellMind API")
                .description("This is Swagger UI, an AI-based wellness personnel management system.")
                .version("1.0.0");
    }
}
