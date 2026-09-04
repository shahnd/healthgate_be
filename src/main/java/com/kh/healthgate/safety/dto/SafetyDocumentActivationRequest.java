package com.kh.healthgate.safety.dto;

import jakarta.validation.constraints.NotNull;

public record SafetyDocumentActivationRequest(@NotNull Boolean active) {
}
