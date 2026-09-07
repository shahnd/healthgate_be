package com.kh.healthgate.safety.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.safety.ai.index.VectorIndexStatus;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "안전문서 조회 응답")
public record SafetyDocumentResponse(
        @Schema(description = "안전문서 ID", example = "1") Long id,
        @Schema(description = "문서 제목", example = "물류센터 지게차 안전수칙") String title,
        @Schema(description = "문서 설명", example = "지게차 운행 및 작업자 안전수칙", nullable = true)
        String description,
        @Schema(description = "업로드 당시 파일명", example = "forklift-safety.pdf") String originalFilename,
        @Schema(description = "파일 미디어 타입", example = "application/pdf") String contentType,
        @Schema(description = "파일 크기(byte)", example = "1048576") long fileSize,
        @Schema(description = "문서 활성 상태", example = "ACTIVE") SafetyDocumentStatus status,
        @Schema(
                description = "벡터 인덱싱 상태. 요청 이력이 없거나 현재 파이프라인과 일치하는 manifest가 없으면 null",
                example = "COMPLETED",
                nullable = true)
        VectorIndexStatus indexStatus,
        @Schema(description = "등록자 직원 ID", example = "1") Long createdById,
        @Schema(description = "최종 수정자 직원 ID", example = "1") Long updatedById,
        @Schema(description = "등록 일시", example = "2026-09-04T09:00:00") LocalDateTime createdAt,
        @Schema(description = "최종 수정 일시", example = "2026-09-04T10:30:00") LocalDateTime updatedAt) {

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
