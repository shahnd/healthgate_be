package com.kh.healthgate.employee.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.employee.model.vo.Employee;


public interface EmployeeDao extends JpaRepository<Employee, Integer> {

    Page<Employee> findByStatus(String status, Pageable pageable);

}
