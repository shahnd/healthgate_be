package com.kh.healthgate.safety.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record SafetyDocumentUpdateRequest(
        @NotBlank @Size(max = 200) String title,
        @Size(max = 2000) String description) {
}
