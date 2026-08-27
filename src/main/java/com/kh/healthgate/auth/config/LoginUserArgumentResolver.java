package com.kh.healthgate.auth.config;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.kh.healthgate.auth.model.vo.LoginUser;
import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {
    private final EmployeeDao employeeDao;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().equals(LoginUser.class);
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            WebDataBinderFactory binderFactory) throws Exception {
        Long employeeId = (Long) webRequest.getAttribute("empId", RequestAttributes.SCOPE_REQUEST);

        if (employeeId == null) {
            return null;
        }

        Employee employee = employeeDao.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("JWT 토큰이 유효하지만 사용자를 찾을 수 없습니다."));

        return LoginUser.from(employee);
    }
}
