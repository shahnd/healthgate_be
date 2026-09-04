package com.kh.healthgate.safety.dto;

import jakarta.validation.constraints.NotNull;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "안전문서 활성 상태 변경 요청")
public record SafetyDocumentActivationRequest(
        @Schema(description = "활성화 여부", example = "true") @NotNull Boolean active) {
}
