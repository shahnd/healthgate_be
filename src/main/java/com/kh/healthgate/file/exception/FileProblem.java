package com.kh.healthgate.file.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

import com.kh.healthgate.common.exception.ProblemDefinition;

public enum FileProblem implements ProblemDefinition {
    UPLOAD_SIZE_EXCEEDED(
            "/problems/file-upload-size-exceeded",
            "파일 업로드 용량 초과",
            HttpStatus.CONTENT_TOO_LARGE,
            "FILE_UPLOAD_SIZE_EXCEEDED",
            "허용된 크기를 초과한 파일입니다.");

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final String code;
    private final String detail;

    FileProblem(String type, String title, HttpStatus status, String code, String detail) {
        this.type = URI.create(type);
        this.title = title;
        this.status = status;
        this.code = code;
        this.detail = detail;
    }

    public URI type() { return type; }
    public String title() { return title; }
    public HttpStatus status() { return status; }
    public String code() { return code; }
    public String detail() { return detail; }
}
