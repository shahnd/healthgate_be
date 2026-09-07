package com.kh.healthgate.safety.event;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kh.healthgate.file.exception.FileStorageException;
import com.kh.healthgate.file.storage.FileStorage;

@ExtendWith(MockitoExtension.class)
class SafetyDocumentDeletedEventListenerTest {
    @Mock
    private FileStorage fileStorage;

    private SafetyDocumentDeletedEventListener listener;

    @BeforeEach
    void setUp() {
        listener = new SafetyDocumentDeletedEventListener(fileStorage);
    }

    @Test
    void deletesFileAfterSafetyDocumentIsDeleted() {
        // given
        SafetyDocumentDeletedEvent event = new SafetyDocumentDeletedEvent("documents/manual.pdf");

        // when
        listener.deleteFile(event);

        // then
        verify(fileStorage).delete("documents/manual.pdf");
    }

    @Test
    void allowsOrphanFileWhenFileDeleteFails() {
        // given
        SafetyDocumentDeletedEvent event = new SafetyDocumentDeletedEvent("documents/manual.pdf");
        doThrow(new FileStorageException("삭제 실패"))
                .when(fileStorage).delete("documents/manual.pdf");

        // when, then
        assertDoesNotThrow(() -> listener.deleteFile(event));
    }
}
