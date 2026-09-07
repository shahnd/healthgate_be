package com.kh.healthgate.safety.dto;

import java.time.LocalDate;

import com.kh.healthgate.safety.domain.SafetyBriefing;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "일일 안전 브리핑 응답")
public record SafetyBriefingResponse(
        @Schema(description = "안전 브리핑 ID", example = "1")
        Long id,
        @Schema(description = "브리핑 기준일", example = "2026-09-04")
        LocalDate briefingDate,
        @Schema(
                description = "날씨와 안전문서를 반영해 생성된 브리핑 내용",
                example = "오늘은 비가 예상됩니다. 미끄럼 사고 예방을 위해 이동 통로를 점검해 주세요.")
        String content) {

    public static SafetyBriefingResponse from(SafetyBriefing briefing) {
        return new SafetyBriefingResponse(
                briefing.getId(),
                briefing.getBriefingDate(),
                briefing.getContent());
    }
}
