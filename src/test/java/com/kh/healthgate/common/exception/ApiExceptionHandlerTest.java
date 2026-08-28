package com.kh.healthgate.common.exception;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.net.URI;

import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

class ApiExceptionHandlerTest {

    private final ApiExceptionHandler handler = new ApiExceptionHandler();

    @Test
    void convertsProblemExceptionToProblemDetail() {
        // given
        ProblemException exception = new TestProblemException("외부에 노출하지 않는 원인");
        ServletWebRequest request = request("/test/resource");

        // when
        ProblemDetail result = handler.handleProblemException(exception, request);

        // then
        assertEquals(503, result.getStatus());
        assertEquals(URI.create("/problems/weather-forecast-unavailable"), result.getType());
        assertEquals("기상예보 조회 실패", result.getTitle());
        assertEquals("오늘 업무시간의 기상예보를 불러오지 못했습니다.", result.getDetail());
        assertEquals(URI.create("/test/resource"), result.getInstance());
        assertEquals("WEATHER_FORECAST_UNAVAILABLE", result.getProperties().get("code"));
    }

    @Test
    void hidesInternalMessageForUnexpectedException() {
        // given
        RuntimeException exception = new RuntimeException("database password leaked");
        ServletWebRequest request = request("/test/resource");

        // when
        ProblemDetail result = handler.handleUnexpectedException(exception, request);

        // then
        assertEquals(500, result.getStatus());
        assertEquals("INTERNAL_SERVER_ERROR", result.getProperties().get("code"));
        assertFalse(result.getDetail().contains(exception.getMessage()));
    }

    private ServletWebRequest request(String uri) {
        return new ServletWebRequest(new MockHttpServletRequest("GET", uri));
    }

    private static class TestProblemException extends ProblemException {
        private TestProblemException(String message) {
            super(ProblemType.WEATHER_FORECAST_UNAVAILABLE, message);
        }
    }
}
