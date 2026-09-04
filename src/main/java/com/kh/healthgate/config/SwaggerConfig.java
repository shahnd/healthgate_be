package com.kh.healthgate.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String JWT_COOKIE_SCHEME_NAME = "JWT_COOKIE";

    @Bean
    public OpenAPI openAPI() {
        SecurityRequirement securityRequirement =
                new SecurityRequirement().addList(JWT_COOKIE_SCHEME_NAME);

        SecurityScheme securityScheme = new SecurityScheme()
                .name("accessToken")
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.COOKIE)
                .description("로그인 시 발급되는 HttpOnly JWT 쿠키");

        return new OpenAPI()
                .info(new Info()
                        .title("HealthGate API")
                        .description("직원 건강관리 시스템 API 문서")
                        .version("v1.0.0"))
                .addSecurityItem(securityRequirement)
                .components(new Components().addSecuritySchemes(JWT_COOKIE_SCHEME_NAME, securityScheme));
    }
}
