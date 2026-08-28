package com.kh.healthgate.config;

import java.util.List;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.kh.healthgate.auth.web.AuthenticatedEmployeeArgumentResolver;
import com.kh.healthgate.employee.model.dao.EmployeeDao;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final EmployeeDao employeeDao;
    private final JwtAuthInterceptor jwtAuthInterceptor;

    public WebConfig(JwtAuthInterceptor jwtAuthInterceptor, EmployeeDao employeeDao) {
        this.jwtAuthInterceptor = jwtAuthInterceptor;
        this.employeeDao = employeeDao;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(jwtAuthInterceptor)
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/auth/login",
                        "/error");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthenticatedEmployeeArgumentResolver(employeeDao));
    }
}
