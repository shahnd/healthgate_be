package com.kh.healthgate.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.healthgate.auth.web.AuthenticatedEmployeeArgumentResolver;

@Configuration
public class WebConfig  implements WebMvcConfigurer{

    private final JwtAuthInterceptor jwtAuthInterceptor;
    private final AuthenticatedEmployeeArgumentResolver authenticatedEmployeeArgumentResolver;

    public WebConfig(
            JwtAuthInterceptor jwtAuthInterceptor,
            AuthenticatedEmployeeArgumentResolver authenticatedEmployeeArgumentResolver) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.authenticatedEmployeeArgumentResolver = authenticatedEmployeeArgumentResolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                            "/auth/login",
                            "/error",
                            "/swagger-ui/**",
                            "/v3/api-docs/**",
                            "/swagger-resources/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(authenticatedEmployeeArgumentResolver);
    }
}
