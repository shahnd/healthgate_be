package com.kh.healthgate.safety.event;

import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kh.healthgate.file.storage.FileStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SafetyDocumentDeletedEventListener {
    private final FileStorage fileStorage;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteFile(SafetyDocumentDeletedEvent event) {
        try {
            fileStorage.delete(event.storageKey());
        } catch (RuntimeException exception) {
            log.error(
                    "삭제된 안전문서의 파일을 정리하지 못했습니다. storageKey={}",
                    event.storageKey(),
                    exception);
        }
    }
}
