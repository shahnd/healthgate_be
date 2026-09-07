package com.kh.healthgate.safety.ai.index;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import com.kh.healthgate.file.storage.FileStorage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class VectorIndexRequestedEventListener {
    private final FileStorage fileStorage;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final VectorIndexManifestService manifestService;
    private final PdfVectorIndexingPipeline indexingPipeline;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void index(VectorIndexRequestedEvent event) {
        String fingerprint = fingerprintFactory.create(event.contentChecksum());
        if (manifestService.isCompleted(fingerprint)) {
            log.info("완료된 벡터 인덱스를 재사용합니다. fingerprint={}", fingerprint);
            return;
        }

        manifestService.startIndexing(fingerprint, event.contentChecksum());
        try {
            int chunkCount = indexingPipeline.index(
                    fileStorage.load(event.storageKey()),
                    fingerprint);
            manifestService.completeIndexing(fingerprint, chunkCount);
        } catch (RuntimeException exception) {
            manifestService.failIndexing(fingerprint, exception.getMessage());
            log.error("안전문서 벡터 인덱싱에 실패했습니다. fingerprint={}", fingerprint, exception);
        }
    }
}
