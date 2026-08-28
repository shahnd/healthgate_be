package com.kh.healthgate.file.storage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.springframework.core.io.Resource;

public interface FileStorage {
    String store(String filename, InputStream inputStream);

    default String store(File file) throws IOException {
        return store(
                file.getName(),
                Files.newInputStream(file.toPath()));
    }

    default String store(Resource resource) throws IOException {
        return store(
                resource.getFilename(),
                resource.getInputStream());
    }

    Resource load(String storageKey);

    void delete(String storageKey);
}
