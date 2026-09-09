package com.kh.healthgate.common.exception;

import java.net.URI;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RFC 9457 API 오류 응답")
public record ApiProblemResponse(
        @Schema(description = "오류 유형 URI", example = "/problems/internal-server-error") URI type,
        @Schema(description = "오류 제목", example = "서버 내부 오류") String title,
        @Schema(description = "HTTP 상태 코드", example = "500") int status,
        @Schema(description = "오류 상세 설명", example = "요청을 처리하는 중 오류가 발생했습니다.") String detail,
        @Schema(description = "오류가 발생한 요청 URI", example = "/healthgate/resource") URI instance,
        @Schema(description = "클라이언트에서 구분할 오류 코드", example = "INTERNAL_SERVER_ERROR", nullable = true)
        String code) {
}
