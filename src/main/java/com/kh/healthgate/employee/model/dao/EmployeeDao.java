package com.kh.healthgate.employee.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.employee.model.vo.Employee;


public interface EmployeeDao extends JpaRepository<Employee, Integer> {

}
