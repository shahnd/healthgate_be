package com.kh.healthgate.safety.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.safety.ai.index.VectorIndexStatus;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;

public record SafetyDocumentResponse(
        Long id,
        String title,
        String description,
        String originalFilename,
        String contentType,
        long fileSize,
        SafetyDocumentStatus status,
        VectorIndexStatus indexStatus,
        Long createdById,
        Long updatedById,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {

    public static SafetyDocumentResponse from(
            SafetyDocument document,
            VectorIndexStatus indexStatus) {
        return new SafetyDocumentResponse(
                document.getId(),
                document.getTitle(),
                document.getDescription(),
                document.getOriginalFilename(),
                document.getContentType(),
                document.getFileSize(),
                document.getStatus(),
                indexStatus,
                document.getCreatedBy().getId(),
                document.getUpdatedBy().getId(),
                document.getCreatedAt(),
                document.getUpdatedAt());
    }
}
