package com.kh.healthgate.safety.event;

import com.kh.healthgate.safety.domain.SafetyDocumentIndexingRequest;

public record VectorIndexRequestedEvent(SafetyDocumentIndexingRequest request) {
}
