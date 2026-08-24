package com.kh.healthgate.auth.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.employee.model.vo.Employee;

public interface AuthDao extends JpaRepository<Employee, Integer> {

    Employee findByEmployeeNumberAndStatus(String employeeNumber, String status);

}
