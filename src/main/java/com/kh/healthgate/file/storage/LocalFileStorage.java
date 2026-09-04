package com.kh.healthgate.file.storage;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.util.Locale;
import java.util.UUID;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.kh.healthgate.file.exception.FileStorageException;

public class LocalFileStorage implements FileStorage {
    private final Path root;

    public LocalFileStorage(Path root) {
        this.root = root.toAbsolutePath().normalize();
    }

    @Override
    public StoredFile store(String filename, String contentType, InputStream inputStream) {
        String storageKey = generateStorageKey(filename);
        Path target = resolve(storageKey);
        Path temporary = target.resolveSibling(target.getFileName() + ".part");

        try {
            long size;

            // 디렉터리 생성
            Files.createDirectories(target.getParent());

            // sha512 다이제스트 생성
            MessageDigest digest = DigestUtils.getSha512Digest();

            // 해싱과 동시에 임시 경로로 파일 복사
            try (DigestInputStream digestInputStream = new DigestInputStream(inputStream, digest)) {
                size = Files.copy(digestInputStream, temporary, StandardCopyOption.REPLACE_EXISTING);
            }

            // digest에서 해시 추출
            String checksum = Hex.encodeHexString(digest.digest());

            // 파일 업로드가 성공했다면 임시 경로에서 타겟 경로로 파일 이동
            move(temporary, target);

            return new StoredFile(
                    storageKey,
                    size,
                    checksum);
        } catch (IOException exception) {
            deleteQuietly(temporary);
            throw new FileStorageException("파일 저장에 실패했습니다: " + filename, exception);
        }
    }

    @Override
    public Resource load(String storageKey) {
        Path target = resolve(storageKey);
        if (!Files.isRegularFile(target)) {
            throw new FileStorageException("저장된 파일을 찾을 수 없습니다: " + storageKey);
        }
        return new FileSystemResource(target);
    }

    @Override
    public void delete(String storageKey) {
        try {
            Files.deleteIfExists(resolve(storageKey));
        } catch (IOException exception) {
            throw new FileStorageException("파일 삭제에 실패했습니다: " + storageKey, exception);
        }
    }

    private String generateStorageKey(String filename) {
        if (!StringUtils.hasText(filename)) {
            throw new FileStorageException("파일명이 필요합니다.");
        }

        String extension = StringUtils.getFilenameExtension(filename);
        if (extension == null) {
            return UUID.randomUUID().toString();
        }

        // sanitize
        extension = extension
                .replaceAll("[^A-Za-z0-9]", "")
                .toLowerCase(Locale.ROOT);

        if (extension.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return UUID.randomUUID() + "." + extension;
    }

    private Path resolve(String storageKey) {
        Path path = root.resolve(storageKey).normalize();
        if (!path.startsWith(root)) {
            throw new FileStorageException("허용되지 않은 파일 경로입니다.");
        }
        return path;
    }

    private void move(Path source, Path target) throws IOException {
        try {
            Files.move(source, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        } catch (AtomicMoveNotSupportedException exception) {
            Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    private void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }
}
