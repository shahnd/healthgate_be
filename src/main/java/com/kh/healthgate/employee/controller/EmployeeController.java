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
import com.kh.healthgate.employee.model.vo.EmpListResponse;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.Positions;
import com.kh.healthgate.employee.model.vo.role;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;


@CrossOrigin
@RestController
@Tag(name = "직원 관리 API", description = "직원 정보 조회, 등록, 수정, 삭제")
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
        @Schema(name = "이름") String name,
        Long departmentId,
        Long positionId,
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate searchDate,
        String status
    ) {}

    public record PasswordUpdateRequest(
        String currentPassword,
        String newPassword
    ) {}

    @GetMapping("/employees")
    @Operation(summary = "직원 목록 조회", description = "검색 조건으로 직원 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<Page<EmpListResponse>>> selectEmployeeList(
        @ModelAttribute EmpSearchCondition condition,
        @RequestParam(defaultValue = "1") int page,
        @RequestParam(defaultValue = "10") int size
    ) {

        Pageable pageable = PageRequest.of(page - 1, size);

        Page<EmpListResponse> list = employeeService.selectEmployeeList(pageable, condition);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @GetMapping("/employees/{id}")
    @Operation(summary = "직원 단건 조회", description = "직원 ID로 직원 단건 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<Employee>> selectEmployee(@PathVariable Long id) {

        Employee emp = employeeService.selectEmployee(id);

        return ResponseEntity.ok(ApiResponse.success(emp));
    }

    @PostMapping("/employees")
    @Operation(summary = "직원 정보 등록", description = "직원 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> insertEmployee(@RequestBody Employee employee) {

        employee.setPassword(bCryptPasswordEncoder.encode(employee.getPassword()));;

        Employee resultEmp = employeeService.insertEmployee(employee);

        String message = (resultEmp != null)? "success" : "fail";

        return ResponseEntity.ok(ApiResponse.successWithNoData(message));
    }

    @PutMapping("/employees/{id}")
    @Operation(summary = "직원 정보 수정", description = "해당 ID의 직원 정보를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateEmployee(@PathVariable Long id, @RequestBody Employee employee) {

        employee.setId(id);
        
        Employee resultEmp = employeeService.updateEmployee(employee);

        return ResponseEntity.ok(ApiResponse.successWithNoData(null));
    }

    @PutMapping("/employees/me/password")
    @Operation(summary = "비밀번호 수정", description = "접속한 계정의 비밀번호를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updatePassword(HttpServletRequest request, @RequestBody PasswordUpdateRequest passwordReq) {
        
        Long userId = (Long)request.getAttribute("empId");
        employeeService.updatePassword(userId, passwordReq.currentPassword(), passwordReq.newPassword());

        return ResponseEntity.ok(ApiResponse.successWithNoData("success"));
    }

    @DeleteMapping("/employees/{id}")
    @Operation(summary = "직원 정보 삭제", description = "직원 정보를 삭제합니다.(소프트삭제)")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long id) {

        employeeService.deleteEmployee(id);

        return ResponseEntity.ok(ApiResponse.successWithNoData(null));
    }

    @GetMapping("/employees/init")
    @Operation(summary = "부서, 직급 리스트 조회", description = "직급, 부서 목록을 조회합니다.")
    public ResponseEntity<ApiResponse<EmpInitList>> selectInitList() {

        List<Departments> departmentList = employeeService.selectDepartmentsList();
        List<Positions> positionList = employeeService.selectPositionsList();

        return ResponseEntity.ok(ApiResponse.success(new EmpInitList(departmentList, positionList)));
    }
    


}
