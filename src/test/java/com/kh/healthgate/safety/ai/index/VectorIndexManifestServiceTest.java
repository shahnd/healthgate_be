package com.kh.healthgate.safety.ai.index;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;

@ExtendWith(MockitoExtension.class)
class VectorIndexManifestServiceTest {
    @Mock
    private VectorIndexManifestRepository repository;

    private VectorIndexManifestService manifestService;

    @BeforeEach
    void setUp() {
        manifestService = new VectorIndexManifestService(repository);
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
}
