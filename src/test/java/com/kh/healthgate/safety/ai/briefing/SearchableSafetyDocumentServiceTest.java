package com.kh.healthgate.safety.ai.briefing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.mock;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

@ExtendWith(MockitoExtension.class)
class SearchableSafetyDocumentServiceTest {
    @Mock
    private SafetyDocumentRepository documentRepository;
    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;
    @Mock
    private VectorIndexManifestService manifestService;

    private SearchableSafetyDocumentService searchableSafetyDocumentService;

    @BeforeEach
    void setUp() {
        searchableSafetyDocumentService = new SearchableSafetyDocumentService(
                documentRepository,
                fingerprintFactory,
                manifestService);
    }

    @Test
    void returnsCompletedActiveDocumentsInRepositoryIdOrder() {
        // given
        SafetyDocument first = mock(SafetyDocument.class);
        SafetyDocument second = mock(SafetyDocument.class);
        SafetyDocument third = mock(SafetyDocument.class);
        when(first.getContentChecksum()).thenReturn("checksum-1");
        when(second.getContentChecksum()).thenReturn("checksum-2");
        when(third.getContentChecksum()).thenReturn("checksum-3");
        when(documentRepository.findByStatusOrderByIdAsc(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of(first, second, third));
        when(fingerprintFactory.create("checksum-1")).thenReturn("fingerprint-c");
        when(fingerprintFactory.create("checksum-2")).thenReturn("fingerprint-a");
        when(fingerprintFactory.create("checksum-3")).thenReturn("fingerprint-b");
        when(manifestService.getCompletedFingerprints(
                List.of("fingerprint-c", "fingerprint-a", "fingerprint-b")))
                .thenReturn(Set.of("fingerprint-c", "fingerprint-a"));

        // when
        List<SafetyDocument> result = searchableSafetyDocumentService.findDocuments();

        // then
        assertThat(result).containsExactly(first, second);
    }

    @Test
    void returnsEmptyListWithoutActiveIndexedDocuments() {
        // given
        when(documentRepository.findByStatusOrderByIdAsc(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of());
        when(manifestService.getCompletedFingerprints(List.of())).thenReturn(Set.of());

        // when
        List<SafetyDocument> result = searchableSafetyDocumentService.findDocuments();

        // then
        assertThat(result).isEmpty();
    }
}
