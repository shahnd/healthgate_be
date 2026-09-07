package com.kh.healthgate.common.exception;

import java.net.URI;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "RFC 9457 API 오류 응답")
public record ApiProblemResponse(
        @Schema(description = "오류 유형 URI", example = "/problems/safety-document-not-found") URI type,
        @Schema(description = "오류 제목", example = "안전문서 없음") String title,
        @Schema(description = "HTTP 상태 코드", example = "404") int status,
        @Schema(description = "오류 상세 설명", example = "요청한 안전문서를 찾을 수 없습니다.") String detail,
        @Schema(description = "오류가 발생한 요청 URI", example = "/healthgate/safety-documents/1") URI instance,
        @Schema(description = "클라이언트에서 구분할 오류 코드", example = "SAFETY_DOCUMENT_NOT_FOUND", nullable = true)
        String code) {
}
