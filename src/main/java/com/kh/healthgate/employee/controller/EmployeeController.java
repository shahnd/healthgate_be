package com.kh.healthgate.employee.controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.common.model.vo.ApiResponse;
import com.kh.healthgate.employee.model.service.EmployeeService;
import com.kh.healthgate.employee.model.vo.Departments;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.Positions;
import com.kh.healthgate.employee.model.vo.role;


@CrossOrigin
@RestController
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public record EmpInitList(
        List<Departments> departmentList,
        List<Positions> positionList
    ) {}

    public record EmpSearchCondition(
        String employeeNumber,
        String name,
        Long departmentId,
        Long positionId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate searchDate,
        String status
    ) {}

    @GetMapping("/employees")
    public ResponseEntity<ApiResponse<Page<Employee>>> selectEmployeeList(
        @ModelAttribute EmpSearchCondition condition,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<Employee> list = employeeService.selectEmployeeList(pageable, condition);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Employee>> selectEmployee(@PathVariable Long id) {

        Employee emp = employeeService.selectEmployee(id);

        return ResponseEntity.ok(ApiResponse.success(emp));
    }

    @PostMapping("/employees")
    public ResponseEntity<ApiResponse<Void>> insertEmployee(@RequestBody Employee employee) {

        employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));;

        Employee resultEmp = employeeService.insertEmployee(employee);

        String message = (resultEmp != null)? "success" : "fail";

        return ResponseEntity.ok(ApiResponse.successWithNoData(message));
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {

        employee.setId(id);
        
        Employee resultEmp = employeeService.updateEmployee(employee);

        return ResponseEntity.ok(ApiResponse.successWithNoData(null));
    }

    @DeleteMapping("/employees/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(ApiResponse.successWithNoData(null));
    }

    @GetMapping("/employees/init")
    public ResponseEntity<ApiResponse<EmpInitList>> selectInitList() {

        List<Departments> departmentList = employeeService.selectDepartmentsList();
        List<Positions> positionList = employeeService.selectPositionsList();

        return ResponseEntity.ok(ApiResponse.success(new EmpInitList(departmentList, positionList)));
    }
    


}
