package com.kh.healthgate.auth.model.vo;

public record AuthenticatedEmployee(
        Long id,
        String employeeNumber,
        String role) {
}
