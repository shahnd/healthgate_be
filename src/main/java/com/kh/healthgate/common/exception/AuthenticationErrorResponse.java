package com.kh.healthgate.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "인증 실패 응답")
public record AuthenticationErrorResponse(
        @Schema(description = "인증 실패 사유", example = "인증 토큰이 누락되었습니다.")
        String message) {
}
