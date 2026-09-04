package com.kh.healthgate.file.web;

import java.net.URI;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import com.kh.healthgate.common.exception.ProblemDetails;
import com.kh.healthgate.file.exception.FileProblem;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class FileUploadExceptionHandler {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ProblemDetail handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request) {
        ProblemDetail detail = ProblemDetails.create(
                FileProblem.UPLOAD_SIZE_EXCEEDED,
                URI.create(request.getRequestURI()));
        if (exception.getMaxUploadSize() >= 0) {
            detail.setProperty("maxSize", exception.getMaxUploadSize());
        }
        return detail;
    }
}
