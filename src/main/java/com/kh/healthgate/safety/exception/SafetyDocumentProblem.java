package com.kh.healthgate.safety.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

import com.kh.healthgate.common.exception.ProblemDefinition;

public enum SafetyDocumentProblem implements ProblemDefinition {
    NOT_FOUND(
            "/problems/safety-document-not-found",
            "안전문서 없음",
            HttpStatus.NOT_FOUND,
            "SAFETY_DOCUMENT_NOT_FOUND",
            "요청한 안전문서를 찾을 수 없습니다."),
    INVALID_FILE(
            "/problems/safety-document-invalid-file",
            "잘못된 안전문서 파일",
            HttpStatus.BAD_REQUEST,
            "SAFETY_DOCUMENT_INVALID_FILE",
            "비어 있지 않은 20MB 이하의 파일을 첨부해 주세요."),
    DUPLICATE_FILE(
            "/problems/safety-document-duplicate-file",
            "중복 안전문서",
            HttpStatus.CONFLICT,
            "SAFETY_DOCUMENT_DUPLICATE_FILE",
            "동일한 내용의 안전문서가 이미 등록되어 있습니다."),
    FORBIDDEN(
            "/problems/safety-document-forbidden",
            "안전문서 등록 권한 없음",
            HttpStatus.FORBIDDEN,
            "SAFETY_DOCUMENT_FORBIDDEN",
            "안전문서를 등록할 권한이 없습니다."),
    STORAGE_FAILED(
            "/problems/safety-document-storage-failed",
            "안전문서 파일 저장 실패",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "SAFETY_DOCUMENT_STORAGE_FAILED",
            "안전문서 파일을 저장하지 못했습니다.");

    private final URI type;
    private final String title;
    private final HttpStatus status;
    private final String code;
    private final String detail;

    SafetyDocumentProblem(String type, String title, HttpStatus status, String code, String detail) {
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
