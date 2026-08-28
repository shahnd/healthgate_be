package com.kh.healthgate.common.exception;

/**
 * RFC 9457 형식의 Exception<br>
 * ApiExceptionHandler에 등록된 패키지에서
 * 이 예외를 throw하면 자동으로 클라이언트로 전달됩니다.
 */
public abstract class ProblemException extends RuntimeException {
    private final ProblemType problemType;

    protected ProblemException(ProblemType problemType, String message) {
        super(message);
        this.problemType = problemType;
    }

    protected ProblemException(ProblemType problemType, String message, Throwable cause) {
        super(message, cause);
        this.problemType = problemType;
    }

    public ProblemType problemType() {
        return problemType;
    }
}
