package com.kh.healthgate.safety.ai.briefing;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

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
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

@ExtendWith(MockitoExtension.class)
class ActiveIndexedSafetyDocumentsTest {
    @Mock
    private SafetyDocumentRepository documentRepository;
    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;
    @Mock
    private VectorIndexManifestService manifestService;

    private ActiveIndexedSafetyDocuments activeIndexedSafetyDocuments;

    @BeforeEach
    void setUp() {
        activeIndexedSafetyDocuments = new ActiveIndexedSafetyDocuments(
                documentRepository,
                fingerprintFactory,
                manifestService);
    }

    @Test
    void returnsSortedCompletedFingerprintsOfActiveDocuments() {
        // given
        when(documentRepository.findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of("checksum-1", "checksum-2", "checksum-3"));
        when(fingerprintFactory.create("checksum-1")).thenReturn("fingerprint-c");
        when(fingerprintFactory.create("checksum-2")).thenReturn("fingerprint-a");
        when(fingerprintFactory.create("checksum-3")).thenReturn("fingerprint-b");
        when(manifestService.getCompletedFingerprints(
                List.of("fingerprint-c", "fingerprint-a", "fingerprint-b")))
                .thenReturn(Set.of("fingerprint-c", "fingerprint-a"));

        // when
        List<String> result = activeIndexedSafetyDocuments.getFingerprints();

        // then
        assertThat(result).containsExactly("fingerprint-a", "fingerprint-c");
    }

    @Test
    void returnsEmptyListWithoutActiveIndexedDocuments() {
        // given
        when(documentRepository.findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of());
        when(manifestService.getCompletedFingerprints(List.of())).thenReturn(Set.of());

        // when
        List<String> result = activeIndexedSafetyDocuments.getFingerprints();

        // then
        assertThat(result).isEmpty();
    }
}
