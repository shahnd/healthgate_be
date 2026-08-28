package com.kh.healthgate.file.storage;

public record StoredFile(
        String storageKey,
        String originalFilename,
        String contentType,
        long size,
        String checksum) {
}
