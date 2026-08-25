package com.kh.healthgate.employee.model.vo;

import java.time.LocalDateTime;

public record EmpListResponse(
    Long id,
    String employeeNumber,
    String name,
    String departmentName,
    String positionName,
    String email,
    LocalDateTime clockInAt,
    String attendanceStatus
) {
    public EmpListResponse(Employee employee) {
        this(
            employee.getId(),
            employee.getEmployeeNumber(),
            employee.getName(),
            employee.getDepartments() != null ? employee.getDepartments().getName() : "부서 미지정",
            employee.getPositions() != null ? employee.getPositions().getName() : "직급 미지정",
            employee.getEmail(),
            !employee.getTimecards().isEmpty() ? employee.getTimecards().get(0).getClockInAt() : null,
            !employee.getTimecards().isEmpty() ? employee.getTimecards().get(0).getStatus() : "미출근"
        );
    }
}
