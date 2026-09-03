package com.kh.healthgate.safety.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.safety.domain.SafetyDocument;

public record SafetyDocumentResponse(
        Long id,
        String title,
        String description,
        String originalFilename,
        String contentType,
        long fileSize,
        Long createdById,
        Long updatedById,
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
                document.getCreatedBy().getId(),
                document.getUpdatedBy().getId(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
