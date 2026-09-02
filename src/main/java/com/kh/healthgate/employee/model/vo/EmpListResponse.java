package com.kh.healthgate.employee.model.vo;

import java.time.LocalDateTime;
import java.util.Comparator;

import com.kh.healthgate.attendance.model.vo.Timecards;

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
            getLatestClockIn(employee),
            getLatestStatus(employee)
        );
    }

    private static LocalDateTime getLatestClockIn(Employee employee) {
        return employee.getTimecards().stream()
            .max(Comparator.comparing(Timecards::getClockInAt))
            .map(Timecards::getClockInAt)
            .orElse(null);
    }

    private static String getLatestStatus(Employee employee) {
        return employee.getTimecards().stream()
            .max(Comparator.comparing(Timecards::getClockInAt))
            .map(Timecards::getStatus)
            .orElse(null);
    }
}
