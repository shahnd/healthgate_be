package com.kh.healthgate.common.exception;

import java.net.URI;

import org.springframework.http.ProblemDetail;

public final class ProblemDetails {
    private ProblemDetails() {
    }

    public static ProblemDetail create(ProblemDefinition problem, URI instance) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(problem.status(), problem.detail());
        detail.setType(problem.type());
        detail.setTitle(problem.title());
        detail.setInstance(instance);
        detail.setProperty("code", problem.code());
        return detail;
    }
}
