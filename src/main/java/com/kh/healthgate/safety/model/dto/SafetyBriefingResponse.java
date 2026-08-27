package com.kh.healthgate.safety.model.dto;

import java.time.LocalDate;

import com.kh.healthgate.safety.model.vo.SafetyBriefing;

public record SafetyBriefingResponse(Long id, LocalDate briefingDate, String content) {

    public static SafetyBriefingResponse from(SafetyBriefing briefing) {
        return new SafetyBriefingResponse(
                briefing.getId(),
                briefing.getBriefingDate(),
                briefing.getContent());
    }
}
