package com.kh.healthgate.file.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.kh.healthgate.file.exception.FileStorageException;

class LocalFileStorageTest {
    @TempDir
    Path temporaryDirectory;

    @Test
    void storesFileWithinRoot() {
        // given
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory);
        byte[] content = "pdf-content".getBytes(StandardCharsets.UTF_8);

        // when
        StoredFile stored = storage.store(
                "manual.PDF",
                "application/pdf",
                new ByteArrayInputStream(content));

        // then
        assertFalse(stored.storageKey().contains("/"));
        assertTrue(stored.storageKey().endsWith(".pdf"));
        assertEquals(content.length, stored.size());
        assertEquals(DigestUtils.sha512Hex(content), stored.checksum());
    }

    @Test
    void loadsStoredFile() throws Exception {
        // given
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory);
        StoredFile stored = storage.store(
                "manual.pdf",
                "application/pdf",
                new ByteArrayInputStream("pdf-content".getBytes(StandardCharsets.UTF_8)));

        // when
        String content = storage.load(stored.storageKey()).getContentAsString(StandardCharsets.UTF_8);

        // then
        assertEquals("pdf-content", content);
    }

    @Test
    void deletesStoredFile() {
        // given
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory);
        StoredFile stored = storage.store(
                "manual.pdf",
                "application/pdf",
                new ByteArrayInputStream("pdf-content".getBytes(StandardCharsets.UTF_8)));

        // when
        storage.delete(stored.storageKey());

        // then
        assertThrows(FileStorageException.class, () -> storage.load(stored.storageKey()));
    }

    @Test
    void rejectsPathTraversal() {
        // given
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory);

        // when, then
        assertThrows(FileStorageException.class, () -> storage.load("../outside.pdf"));
        assertThrows(FileStorageException.class, () -> storage.delete("../outside.pdf"));
    }

    @Test
    void rejectsMissingFilename() {
        // given
        LocalFileStorage storage = new LocalFileStorage(temporaryDirectory);
        byte[] content = "file-content".getBytes(StandardCharsets.UTF_8);

        // when, then
        assertThrows(FileStorageException.class, () -> storage.store(
                null, "application/octet-stream", new ByteArrayInputStream(content)));
        assertThrows(FileStorageException.class, () -> storage.store(
                "  ", "application/octet-stream", new ByteArrayInputStream(content)));
    }
}
