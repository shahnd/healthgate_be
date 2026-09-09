package com.kh.healthgate.opendata.weather.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

import com.kh.healthgate.common.exception.ProblemDefinition;

public enum WeatherProblem implements ProblemDefinition {
    WEATHER_FORECAST_UNAVAILABLE(
            "/problems/weather-forecast-unavailable",
            "기상예보 조회 실패",
            HttpStatus.SERVICE_UNAVAILABLE,
            "WEATHER_FORECAST_UNAVAILABLE",
            "오늘 업무시간의 기상예보를 불러오지 못했습니다.");

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final String code;
    private final String detail;

    WeatherProblem(String type, String title, HttpStatus status, String code, String detail) {
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
