package com.kh.healthgate.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class LocalFileStorage implements FileStorage {
    private final Path root;

    public LocalFileStorage(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    private String generateStorageKey(String filename) {
        String extension = FilenameUtils.getExtension(filename);
        String suffix = extension.isBlank() ? "" : "." + extension;
        return String.format("documents/%s%s", UUID.randomUUID(), suffix);
    }

    private Path resolveStorageKey(String storageKey) {
        Path path = root.resolve(storageKey).normalize();

        if (!path.startsWith(root)) {
            throw new IllegalArgumentException();
        }

        return path;
    }

    @Override
    public String store(String filename, InputStream in) {
        String storageKey = generateStorageKey(filename);
        Path target = resolveStorageKey(storageKey);

        try {
            Files.createDirectories(target.getParent());
            Files.copy(in, target);
            return storageKey;
        } catch (IOException e) {
            throw new RuntimeException("파일 저장 실패: " + filename, e);
        }
    }

    @Override
    public Resource load(String storageKey) {
        Path target = resolveStorageKey(storageKey);

        if (!Files.exists(target)) {
            throw new IllegalArgumentException("파일이 존재하지 않습니다: " + storageKey);
        }

        return new FileSystemResource(target);
    }

    @Override
    public void delete(String storageKey) {
        Path target = resolveStorageKey(storageKey);

        try {
            Files.delete(target);
        } catch (IOException e) {
            throw new RuntimeException("파일 삭제 실패: " + storageKey, e);
        }
    }
}
