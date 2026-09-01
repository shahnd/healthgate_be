package com.kh.healthgate.safety.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.safety.domain.SafetyDocument;

public record SafetyDocumentResponse(
        Long id,
        String title,
        String description,
        String originalFilename,
        String contentType,
        long fileSize,
        EmployeeResponse createdBy,
        EmployeeResponse updatedBy,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static SafetyDocumentResponse from(SafetyDocument document) {
        return new SafetyDocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getOriginalFilename(),
                document.getContentType(),
                document.getFileSize(),
                EmployeeResponse.from(document.getCreatedBy()),
                EmployeeResponse.from(document.getUpdatedBy()),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }

    public record EmployeeResponse(Long id, String employeeNumber, String name) {
        private static EmployeeResponse from(Employee employee) {
            return new EmployeeResponse(
                    employee.getId(),
                    employee.getEmployeeNumber(),
                    employee.getName());
        }
    }
}
