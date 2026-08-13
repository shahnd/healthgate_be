package com.kh.healthgate.employee.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;

@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/employees")
    public ResponseEntity<List<Employee>> selectEmployeeList() {

        List<Employee> list = employeeService.selectEmployeeList();

        return ResponseEntity.ok(list);
    }

    @PostMapping("/employees")
    public ResponseEntity<String> insertEmployee(@RequestBody Employee employee) {

        employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));;

        Employee resultEmp = employeeService.insertEmployee(employee);

        String message = (resultEmp != null)? "success" : "fail";

        return ResponseEntity.ok(message);
    }

}
