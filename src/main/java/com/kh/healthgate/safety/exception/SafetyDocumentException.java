package com.kh.healthgate.safety.exception;

import com.kh.healthgate.common.exception.ProblemException;

public class SafetyDocumentException extends ProblemException {
    public SafetyDocumentException(SafetyDocumentProblem problem) {
        super(problem, problem.detail());
    }

    public SafetyDocumentException(SafetyDocumentProblem problem, Throwable cause) {
        super(problem, problem.detail(), cause);
    }
}
