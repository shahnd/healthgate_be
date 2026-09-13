package com.kh.healthgate.safety.domain;

/** 인덱싱 요청 시점의 문서 정보 스냅샷. */
public record SafetyDocumentIndexingRequest(
        String storageKey,
        String title,
        String originalFilename,
        String contentChecksum,
        String fingerprint) {
}
