package com.kh.healthgate.safety.ai.index;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;

import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VectorIndexManifestService {
    private static final Duration HEARTBEAT_TIMEOUT = Duration.ofMinutes(3);

    private final VectorIndexManifestRepository repository;
    private final Clock clock;

    @Transactional
    public VectorIndexStatus acceptIndexingRequest(String fingerprint, String contentChecksum) {
        if (repository.retryIndexing(fingerprint)) {
            return VectorIndexStatus.PENDING;
        }

        if (repository.existsById(fingerprint)) {
            throw indexingRequestConflict();
        }

        try {
            repository.saveAndFlush(new VectorIndexManifest(fingerprint, contentChecksum));
            return VectorIndexStatus.PENDING;
        } catch (DataIntegrityViolationException exception) {
            // 동일 fingerprint가 동시에 생성되면 기본키 충돌을 도메인 충돌로 변환합니다.
            throw new SafetyDocumentException(
                    SafetyDocumentProblem.INDEXING_REQUEST_CONFLICT,
                    exception);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<VectorIndexStatus> getStatus(String fingerprint) {
        return repository.findById(fingerprint)
                .map(this::resolveStatus);
    }

    @Transactional(readOnly = true)
    public Map<String, VectorIndexStatus> getStatuses(Collection<String> fingerprints) {
        return repository.findAllById(fingerprints).stream()
                .collect(Collectors.toMap(
                        manifest -> manifest.getFingerprint(),
                        manifest -> manifest.getStatus()));
    }

    @Transactional(readOnly = true)
    public boolean isCompleted(String fingerprint) {
        return repository.findById(fingerprint)
                .filter(manifest -> manifest.isCompleted())
                .isPresent();
    }

    @Transactional(readOnly = true)
    public Set<String> getCompletedFingerprints(Collection<String> fingerprints) {
        return repository.findAllById(fingerprints).stream()
                .filter(manifest -> manifest.isCompleted())
                .map(manifest -> manifest.getFingerprint())
                .collect(Collectors.toSet());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public boolean startIndexing(String fingerprint) {
        return repository.startIndexing(fingerprint);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeIndexing(String fingerprint, int chunkCount) {
        if (!repository.completeIndexing(fingerprint, chunkCount)) {
            throwIfCancellationRequested(fingerprint);
            throw new IllegalStateException("인덱싱 완료 상태를 저장할 수 없습니다.");
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failIndexing(String fingerprint, String failureMessage) {
        if (!repository.failIndexing(fingerprint, truncate(failureMessage))) {
            completeCancellation(fingerprint);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void heartbeat(String fingerprint) {
        if (!repository.updateHeartbeat(fingerprint)) {
            throwIfCancellationRequested(fingerprint);
            throw new IllegalStateException("인덱싱 작업이 실행 상태가 아닙니다.");
        }
    }

    @Transactional
    public VectorIndexStatus requestCancellation(String fingerprint) {
        if (repository.cancelPendingIndexing(fingerprint)) {
            return VectorIndexStatus.CANCELLED;
        }

        if (repository.cancelHangingIndexing(
                fingerprint,
                heartbeatDeadline())) {
            return VectorIndexStatus.CANCELLED;
        }

        if (repository.requestCancellation(fingerprint)) {
            return VectorIndexStatus.CANCEL_REQUESTED;
        }

        return repository.findById(fingerprint)
                .map(m -> m.getStatus())
                .filter(status -> status == VectorIndexStatus.CANCEL_REQUESTED
                        || status == VectorIndexStatus.CANCELLED)
                .orElseThrow(this::indexingCancellationConflict);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeCancellation(String fingerprint) {
        repository.completeCancellation(fingerprint);
    }

    private SafetyDocumentException indexingRequestConflict() {
        return new SafetyDocumentException(SafetyDocumentProblem.INDEXING_REQUEST_CONFLICT);
    }

    private SafetyDocumentException indexingCancellationConflict() {
        return new SafetyDocumentException(SafetyDocumentProblem.INDEXING_CANCELLATION_CONFLICT);
    }

    private void throwIfCancellationRequested(String fingerprint) {
        repository.findById(fingerprint)
                .filter(manifest -> manifest.getStatus() == VectorIndexStatus.CANCEL_REQUESTED)
                .ifPresent(manifest -> {
                    throw new VectorIndexingCancelledException();
                });
    }

    private String truncate(String message) {
        if (message == null || message.length() <= 1000) {
            return message;
        }
        return message.substring(0, 1000);
    }

    private boolean isHanging(VectorIndexManifest manifest) {
        return (manifest
                .getStatus() == VectorIndexStatus.INDEXING
                || manifest.getStatus() == VectorIndexStatus.CANCEL_REQUESTED)
                && manifest.getUpdatedAt().isBefore(heartbeatDeadline());
    }

    private VectorIndexStatus resolveStatus(VectorIndexManifest manifest) {
        if (!isHanging(manifest)) {
            return manifest.getStatus();
        }

        if (manifest.getStatus() == VectorIndexStatus.CANCEL_REQUESTED) {
            boolean updated = repository.completeCancellation(manifest.getFingerprint());
            return updated ? VectorIndexStatus.CANCELLED : manifest.getStatus();
        }

        boolean updated = repository.failIndexing(
                manifest.getFingerprint(),
                "인덱싱 heartbeat가 만료되었습니다.");
        return updated ? VectorIndexStatus.FAILED : manifest.getStatus();
    }

    private LocalDateTime heartbeatDeadline() {
        return LocalDateTime.now(clock).minus(HEARTBEAT_TIMEOUT);
    }
}
