package com.kh.healthgate.common.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;

public interface ProblemDefinition {
    URI type();

    String title();

    HttpStatus status();

    String code();

    String detail();
}
