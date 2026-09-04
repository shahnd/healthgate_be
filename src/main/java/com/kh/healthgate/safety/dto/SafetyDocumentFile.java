package com.kh.healthgate.safety.dto;

import org.springframework.core.io.Resource;

public record SafetyDocumentFile(
        Resource resource,
        String filename,
        String contentType,
        long size) {
}
