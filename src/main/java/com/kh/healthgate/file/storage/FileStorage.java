package com.kh.healthgate.file.storage;

import java.io.IOException;
import java.io.InputStream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.file.exception.FileStorageException;

public interface FileStorage {
    StoredFile store(String filename, String contentType, InputStream inputStream);

    default StoredFile store(MultipartFile file) {
        try {
            return store(
                    file.getOriginalFilename(),
                    file.getContentType(),
                    file.getInputStream());
        } catch (IOException exception) {
            throw new FileStorageException("업로드 파일을 읽지 못했습니다.", exception);
        }
    }

    Resource load(String storageKey);

    void delete(String storageKey);
}
