package com.kh.healthgate.safety.ai.index;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;

@ExtendWith(MockitoExtension.class)
class VectorIndexManifestServiceTest {
    private static final Clock CLOCK = Clock.fixed(
            Instant.parse("2026-09-08T06:00:00Z"),
            ZoneOffset.UTC);

    @Mock
    private VectorIndexManifestRepository repository;

    private VectorIndexManifestService manifestService;

    @BeforeEach
    void setUp() {
        manifestService = new VectorIndexManifestService(repository, CLOCK);
    }

    @Test
    void acceptsFirstIndexingRequest() {
        // given
        when(repository.retryIndexing("fingerprint")).thenReturn(false);
        when(repository.existsById("fingerprint")).thenReturn(false);

        // when
        VectorIndexStatus status = manifestService.acceptIndexingRequest(
                "fingerprint",
                "checksum");

        // then
        assertSame(VectorIndexStatus.PENDING, status);
        verify(repository).saveAndFlush(org.mockito.ArgumentMatchers.argThat(manifest ->
                manifest.getFingerprint().equals("fingerprint")
                        && manifest.getContentChecksum().equals("checksum")
                        && manifest.getStatus() == VectorIndexStatus.PENDING));
    }

    @Test
    void acceptsRetryWhenFailedOrCancelledManifestMovesToPending() {
        // given
        when(repository.retryIndexing("fingerprint")).thenReturn(true);

        // when
        VectorIndexStatus status = manifestService.acceptIndexingRequest(
                "fingerprint",
                "checksum");

        // then
        assertSame(VectorIndexStatus.PENDING, status);
        verify(repository, never()).existsById("fingerprint");
        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void rejectsRequestWhenManifestIsAlreadyPresent() {
        // given
        when(repository.retryIndexing("fingerprint")).thenReturn(false);
        when(repository.existsById("fingerprint")).thenReturn(true);

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> manifestService.acceptIndexingRequest("fingerprint", "checksum"));

        // then
        assertSame(SafetyDocumentProblem.INDEXING_REQUEST_CONFLICT, exception.problemType());
        verify(repository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void convertsConcurrentManifestCreationToConflict() {
        // given
        when(repository.retryIndexing("fingerprint")).thenReturn(false);
        when(repository.existsById("fingerprint")).thenReturn(false);
        when(repository.saveAndFlush(org.mockito.ArgumentMatchers.any()))
                .thenThrow(new DataIntegrityViolationException("duplicate fingerprint"));

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> manifestService.acceptIndexingRequest("fingerprint", "checksum"));

        // then
        assertSame(SafetyDocumentProblem.INDEXING_REQUEST_CONFLICT, exception.problemType());
    }

    @Test
    void cancelsPendingIndexingImmediately() {
        // given
        when(repository.cancelPendingIndexing("fingerprint")).thenReturn(true);

        // when
        VectorIndexStatus status = manifestService.requestCancellation("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCELLED, status);
    }

    @Test
    void requestsCancellationForRunningIndexing() {
        // given
        when(repository.cancelPendingIndexing("fingerprint")).thenReturn(false);
        when(repository.requestCancellation("fingerprint")).thenReturn(true);

        // when
        VectorIndexStatus status = manifestService.requestCancellation("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCEL_REQUESTED, status);
    }

    @Test
    void cancelsHangingIndexingImmediately() {
        // given
        when(repository.cancelPendingIndexing("fingerprint")).thenReturn(false);
        when(repository.cancelHangingIndexing(
                "fingerprint",
                LocalDateTime.of(2026, 9, 8, 5, 57))).thenReturn(true);

        // when
        VectorIndexStatus status = manifestService.requestCancellation("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCELLED, status);
    }

    @Test
    void resolvesHangingIndexingAsFailedWhenStatusIsRead() {
        // given
        VectorIndexManifest manifest = mock(VectorIndexManifest.class);
        when(manifest.getFingerprint()).thenReturn("fingerprint");
        when(manifest.getStatus()).thenReturn(VectorIndexStatus.INDEXING);
        when(manifest.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 9, 8, 5, 55));
        when(repository.findById("fingerprint")).thenReturn(Optional.of(manifest));
        when(repository.failIndexing(
                "fingerprint",
                "인덱싱 heartbeat가 만료되었습니다.")).thenReturn(true);

        // when
        Optional<VectorIndexStatus> status = manifestService.getStatus("fingerprint");

        // then
        assertSame(VectorIndexStatus.FAILED, status.orElseThrow());
    }

    @Test
    void resolvesHangingCancellationAsCancelledWhenStatusIsRead() {
        // given
        VectorIndexManifest manifest = mock(VectorIndexManifest.class);
        when(manifest.getFingerprint()).thenReturn("fingerprint");
        when(manifest.getStatus()).thenReturn(VectorIndexStatus.CANCEL_REQUESTED);
        when(manifest.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 9, 8, 5, 55));
        when(repository.findById("fingerprint")).thenReturn(Optional.of(manifest));
        when(repository.completeCancellation("fingerprint")).thenReturn(true);

        // when
        Optional<VectorIndexStatus> status = manifestService.getStatus("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCELLED, status.orElseThrow());
    }
}
