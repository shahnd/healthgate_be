package com.kh.healthgate.file;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.springframework.core.io.Resource;

public interface FileStorage {
    public String store(String filename, InputStream InputStream);

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

    public Resource load(String storageKey);

    public void delete(String storageKey);
}
