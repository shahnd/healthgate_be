package com.kh.healthgate.employee.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;

@Service
public class EmployeeService {

    @Autowired
    private EmployeeDao employeeDao;

    public List<Employee> selectEmployeeList() {

        return employeeDao.findAll();
    }

    public Employee insertEmployee(Employee employee) {
        return employeeDao.save(employee);
    }


    
}
