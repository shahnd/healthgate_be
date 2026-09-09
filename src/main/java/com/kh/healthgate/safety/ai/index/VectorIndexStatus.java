package com.kh.healthgate.safety.ai.index;

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
