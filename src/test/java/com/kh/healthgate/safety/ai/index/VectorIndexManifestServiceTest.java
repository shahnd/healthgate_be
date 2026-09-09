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
        when(repository.retryFailed(
                "fingerprint",
                VectorIndexStatus.FAILED,
                VectorIndexStatus.PENDING)).thenReturn(0);
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
    void acceptsRetryWhenFailedManifestMovesToPending() {
        // given
        when(repository.retryFailed(
                "fingerprint",
                VectorIndexStatus.FAILED,
                VectorIndexStatus.PENDING)).thenReturn(1);

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
        when(repository.retryFailed(
                "fingerprint",
                VectorIndexStatus.FAILED,
                VectorIndexStatus.PENDING)).thenReturn(0);
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
        when(repository.retryFailed(
                "fingerprint",
                VectorIndexStatus.FAILED,
                VectorIndexStatus.PENDING)).thenReturn(0);
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
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.PENDING,
                VectorIndexStatus.CANCELLED)).thenReturn(1);

        // when
        VectorIndexStatus status = manifestService.requestCancellation("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCELLED, status);
    }

    @Test
    void requestsCancellationForRunningIndexing() {
        // given
        VectorIndexManifest manifest = mock(VectorIndexManifest.class);
        when(manifest.getStatus()).thenReturn(VectorIndexStatus.INDEXING);
        when(manifest.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 9, 8, 5, 59));
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.PENDING,
                VectorIndexStatus.CANCELLED)).thenReturn(0);
        when(repository.findById("fingerprint")).thenReturn(Optional.of(manifest));
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.INDEXING,
                VectorIndexStatus.CANCEL_REQUESTED)).thenReturn(1);

        // when
        VectorIndexStatus status = manifestService.requestCancellation("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCEL_REQUESTED, status);
    }

    @Test
    void cancelsHangingIndexingImmediately() {
        // given
        VectorIndexManifest manifest = mock(VectorIndexManifest.class);
        when(manifest.getStatus()).thenReturn(VectorIndexStatus.INDEXING);
        when(manifest.getUpdatedAt()).thenReturn(LocalDateTime.of(2026, 9, 8, 5, 55));
        when(repository.findById("fingerprint")).thenReturn(Optional.of(manifest));
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.PENDING,
                VectorIndexStatus.CANCELLED)).thenReturn(0);
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.INDEXING,
                VectorIndexStatus.CANCELLED)).thenReturn(1);

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
                "인덱싱 heartbeat가 만료되었습니다.",
                VectorIndexStatus.INDEXING,
                VectorIndexStatus.FAILED)).thenReturn(1);

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
        when(repository.transitionStatus(
                "fingerprint",
                VectorIndexStatus.CANCEL_REQUESTED,
                VectorIndexStatus.CANCELLED)).thenReturn(1);

        // when
        Optional<VectorIndexStatus> status = manifestService.getStatus("fingerprint");

        // then
        assertSame(VectorIndexStatus.CANCELLED, status.orElseThrow());
    }
}
