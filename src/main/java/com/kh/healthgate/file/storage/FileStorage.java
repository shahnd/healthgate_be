package com.kh.healthgate.file.storage;

import java.io.InputStream;

import org.springframework.core.io.Resource;

public interface FileStorage {
    StoredFile store(String filename, String contentType, InputStream inputStream);

    Resource load(String storageKey);

    void delete(String storageKey);
}
