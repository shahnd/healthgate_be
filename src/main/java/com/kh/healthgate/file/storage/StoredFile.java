package com.kh.healthgate.file.storage;

public record StoredFile(
        String storageKey,
        long size,
        String checksum) {
}
