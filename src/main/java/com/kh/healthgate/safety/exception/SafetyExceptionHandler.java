package com.kh.healthgate.safety.exception;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.kh.healthgate.opendata.weather.exceptions.WeatherApiException;
import com.kh.healthgate.opendata.weather.exceptions.WeatherForecastException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = "com.kh.healthgate.safety")
public class SafetyExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler({ WeatherApiException.class, WeatherForecastException.class })
    protected ProblemDetail handleWeatherException(RuntimeException exception, WebRequest request) {
        return createSafetyProblemDetail(
                exception,
                SafetyProblemType.WEATHER_FORECAST_UNAVAILABLE,
                request);
    }

    @ExceptionHandler(SafetyBriefingGenerationException.class)
    protected ProblemDetail handleSafetyBriefingGenerationException(
            SafetyBriefingGenerationException exception,
            WebRequest request) {
        return createSafetyProblemDetail(
                exception,
                SafetyProblemType.SAFETY_BRIEFING_GENERATION_FAILED,
                request);
    }

    @ExceptionHandler(Exception.class)
    protected ProblemDetail handleUnexpectedException(Exception exception, WebRequest request) {
        log.error("안전 API 처리 중 예상하지 못한 오류가 발생했습니다.", exception);
        return createSafetyProblemDetail(
                exception,
                SafetyProblemType.INTERNAL_SERVER_ERROR,
                request);
    }

    private ProblemDetail createSafetyProblemDetail(
            Exception exception,
            SafetyProblemType problemType,
            WebRequest request) {
        ProblemDetail problemDetail = createProblemDetail(
                exception,
                problemType.status(),
                problemType.detail(),
                null,
                null,
                request);

        problemDetail.setType(problemType.type());
        problemDetail.setTitle(problemType.title());
        problemDetail.setInstance(requestUri(request));
        problemDetail.setProperty("code", problemType.code());

        return problemDetail;
    }

    private URI requestUri(WebRequest request) {
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        return URI.create(servletWebRequest.getRequest().getRequestURI());
    }
}
