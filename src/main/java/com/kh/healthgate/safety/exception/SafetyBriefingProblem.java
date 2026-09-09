package com.kh.healthgate.safety.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

import com.kh.healthgate.common.exception.ProblemDefinition;

public enum SafetyBriefingProblem implements ProblemDefinition {
    SAFETY_BRIEFING_GENERATION_FAILED(
            "/problems/safety-briefing-generation-failed",
            "안전 브리핑 생성 실패",
            HttpStatus.SERVICE_UNAVAILABLE,
            "SAFETY_BRIEFING_GENERATION_FAILED",
            "오늘의 안전 브리핑을 생성하지 못했습니다.");

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final String code;
    private final String detail;

    SafetyBriefingProblem(String type, String title, HttpStatus status, String code, String detail) {
        this.type = URI.create(type);
        this.title = title;
        this.status = status;
        this.code = code;
        this.detail = detail;
    }

    public URI type() {
        return type;
    }

    public String title() {
        return title;
    }

    public HttpStatus status() {
        return status;
    }

    public String code() {
        return code;
    }

    public String detail() {
        return detail;
    }
}
