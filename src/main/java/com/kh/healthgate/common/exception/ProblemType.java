package com.kh.healthgate.common.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

public enum ProblemType {
    WEATHER_FORECAST_UNAVAILABLE(
            "/problems/weather-forecast-unavailable",
            "기상예보 조회 실패",
            HttpStatus.SERVICE_UNAVAILABLE,
            "WEATHER_FORECAST_UNAVAILABLE",
            "오늘 업무시간의 기상예보를 불러오지 못했습니다."),
    SAFETY_BRIEFING_GENERATION_FAILED(
            "/problems/safety-briefing-generation-failed",
            "안전 브리핑 생성 실패",
            HttpStatus.SERVICE_UNAVAILABLE,
            "SAFETY_BRIEFING_GENERATION_FAILED",
            "오늘의 안전 브리핑을 생성하지 못했습니다."),
    INTERNAL_SERVER_ERROR(
            "/problems/internal-server-error",
            "서버 내부 오류",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "INTERNAL_SERVER_ERROR",
            "요청을 처리하는 중 오류가 발생했습니다.");

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final String code;
    private final String detail;

    ProblemType(String type, String title, HttpStatus status, String code, String detail) {
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
