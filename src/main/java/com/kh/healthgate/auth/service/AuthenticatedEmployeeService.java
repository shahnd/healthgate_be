package com.kh.healthgate.auth.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticatedEmployeeService {
    private final EmployeeDao employeeDao;

    @Transactional(readOnly = true)
    public Employee getLoggedInEmployee(AuthenticatedEmployee authenticatedEmployee) {
        if (authenticatedEmployee == null) {
            throw new IllegalStateException("인증된 직원 정보가 없습니다.");
        }

        return employeeDao.findById(authenticatedEmployee.id())
                .filter(employee -> "Y".equals(employee.getStatus()))
                .orElseThrow(() -> new IllegalStateException("인증된 재직 직원을 찾을 수 없습니다."));
    }
}
