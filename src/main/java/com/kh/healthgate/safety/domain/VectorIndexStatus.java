package com.kh.healthgate.safety.domain;

public enum VectorIndexStatus {
    PENDING,
    INDEXING,
    CANCEL_REQUESTED,
    CANCELLED,
    COMPLETED,
    FAILED,
    PURGING,
    PURGE_FAILED
}
