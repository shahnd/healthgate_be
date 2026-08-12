package com.kh.healthgate.auth.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.auth.model.dao.AuthDao;
import com.kh.healthgate.employee.model.vo.Employee;

@Service
public class AuthService {

    @Autowired
    private AuthDao authDao;

    public Employee loginEmployee(String employeeNo) {
        return authDao.findByEmployeeNoAndEmployeeStatus(employeeNo, "Y");
    }

}
