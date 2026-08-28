package com.kh.healthgate.safety.exception;

import com.kh.healthgate.common.exception.ProblemException;
import com.kh.healthgate.common.exception.ProblemType;

public class SafetyBriefingGenerationException extends ProblemException {

    public SafetyBriefingGenerationException(Throwable cause) {
        super(
                ProblemType.SAFETY_BRIEFING_GENERATION_FAILED,
                "안전 브리핑 생성에 실패했습니다.",
                cause);
    }
}
