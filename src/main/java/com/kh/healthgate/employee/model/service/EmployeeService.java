package com.kh.healthgate.employee.model.service;

import com.kh.healthgate.employee.model.dao.PositionsDao;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import com.kh.healthgate.employee.controller.EmployeeController.EmpSearchCondition;
import com.kh.healthgate.employee.model.dao.DepartmentsDao;
import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.dao.EmployeeSpecification;
import com.kh.healthgate.employee.model.vo.Departments;
import com.kh.healthgate.employee.model.vo.EmpListResponse;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.Positions;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final DepartmentsDao departmentsDao;
    private final PositionsDao positionsDao;
    private final EmployeeDao employeeDao;

    public Page<EmpListResponse> selectEmployeeList(Pageable pageable, EmpSearchCondition condition) {
        Specification<Employee> spec = EmployeeSpecification.search(condition);
        Page<Employee> employPage = employeeDao.findAll(spec, pageable);
        return employPage.map(EmpListResponse::new);
    }

    @Transactional
    public Employee insertEmployee(Employee employee) {
        return employeeDao.save(employee);
    }

    public Employee selectEmployee(Long id) {
        return employeeDao.findById(id).orElse(null);
    }

    @Transactional
    public Employee updateEmployee(Employee employee) {
        return employeeDao.save(employee);
    }

    @Transactional
    public void deleteEmployee(Long id) {

        Employee employee = employeeDao.findById(id).orElseThrow(() -> new IllegalArgumentException("해당 직원이 존재하지 않습니다."));

        employee.changeStatusToInactive();
    }

    public List<Departments> selectDepartmentsList() {
        return departmentsDao.findAll();
    }
    
    public List<Positions> selectPositionsList() {
        return positionsDao.findAll();
    }


    
}
