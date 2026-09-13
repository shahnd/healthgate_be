package com.kh.healthgate.safety.event;

public record VectorIndexRequestedEvent(
        String storageKey,
        String contentChecksum) {
}
