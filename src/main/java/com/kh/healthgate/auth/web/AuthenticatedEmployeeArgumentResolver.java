package com.kh.healthgate.auth.web;

import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;

@Component
public class AuthenticatedEmployeeArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(AuthenticatedEmployee.class);
    }

    @Override
    public AuthenticatedEmployee resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) {
        return new AuthenticatedEmployee(
                (Long) webRequest.getAttribute("empId", RequestAttributes.SCOPE_REQUEST),
                (String) webRequest.getAttribute("employeeNumber", RequestAttributes.SCOPE_REQUEST),
                (String) webRequest.getAttribute("empRole", RequestAttributes.SCOPE_REQUEST));
    }
}
