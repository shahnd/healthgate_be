package com.kh.healthgate.common.exception;

import java.net.URI;

import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice(basePackages = {
        // RFC 9457 공통 예외 처리를 사용할 API 패키지 등록
        "com.kh.healthgate.safety"
})
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(ProblemException.class)
    protected ProblemDetail handleProblemException(ProblemException exception, WebRequest request) {
        return createApiProblemDetail(exception, exception.problemType(), request);
    }

    @ExceptionHandler(Exception.class)
    protected ProblemDetail handleUnexpectedException(Exception exception, WebRequest request) {
        log.error("API 처리 중 예상하지 못한 오류가 발생했습니다.", exception);
        return createApiProblemDetail(exception, ProblemType.INTERNAL_SERVER_ERROR, request);
    }

    private ProblemDetail createApiProblemDetail(
            Exception exception,
            ProblemDefinition problemType,
            WebRequest request) {
        return ProblemDetails.create(problemType, requestUri(request));
    }

    private URI requestUri(WebRequest request) {
        // 현재는 Servlet을 WebRequest 구현체로 사용하므로 형변환하여 URI 추출
        ServletWebRequest servletWebRequest = (ServletWebRequest) request;
        return URI.create(servletWebRequest.getRequest().getRequestURI());
    }
}
