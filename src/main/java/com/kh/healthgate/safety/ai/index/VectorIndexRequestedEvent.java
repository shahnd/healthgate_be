package com.kh.healthgate.safety.ai.index;

public record VectorIndexRequestedEvent(
        String storageKey,
        String contentChecksum) {
}
