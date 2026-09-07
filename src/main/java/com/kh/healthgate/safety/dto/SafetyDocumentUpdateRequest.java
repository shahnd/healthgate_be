package com.kh.healthgate.safety.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "안전문서 메타데이터 수정 요청")
public record SafetyDocumentUpdateRequest(
        @Schema(description = "문서 제목", example = "개정된 지게차 안전수칙")
        @NotBlank @Size(max = 200) String title,
        @Schema(description = "문서 설명", example = "2026년 개정 안전수칙")
        @Size(max = 2000) String description) {
}
