package com.kh.healthgate.safety.exception;

public class SafetyBriefingGenerationException extends RuntimeException {

    public SafetyBriefingGenerationException(Throwable cause) {
        super("안전 브리핑 생성에 실패했습니다.", cause);
    }
}
