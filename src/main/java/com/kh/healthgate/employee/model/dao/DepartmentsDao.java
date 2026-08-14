package com.kh.healthgate.employee.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.employee.model.vo.Departments;

public interface DepartmentsDao extends JpaRepository<Departments, Integer> {

}