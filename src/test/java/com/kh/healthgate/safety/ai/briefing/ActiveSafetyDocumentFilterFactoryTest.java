package com.kh.healthgate.safety.ai.briefing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

@ExtendWith(MockitoExtension.class)
class ActiveSafetyDocumentFilterFactoryTest {
    @Mock
    private SafetyDocumentRepository documentRepository;

    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;

    @Mock
    private VectorIndexManifestService manifestService;

    private ActiveSafetyDocumentFilterFactory filterFactory;

    @BeforeEach
    void setUp() {
        filterFactory = new ActiveSafetyDocumentFilterFactory(
                documentRepository,
                fingerprintFactory,
                manifestService);
    }

    @Test
    void filtersWithCompletedIndexesOfActiveDocuments() {
        // given
        when(documentRepository.findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of("checksum-1", "checksum-2"));
        when(fingerprintFactory.create("checksum-1")).thenReturn("fingerprint-1");
        when(fingerprintFactory.create("checksum-2")).thenReturn("fingerprint-2");
        when(manifestService.getCompletedFingerprints(
                List.of("fingerprint-1", "fingerprint-2")))
                .thenReturn(Set.of("fingerprint-1"));

        // when
        Filter.Expression result = filterFactory.create();

        // then
        Filter.Expression expected = new FilterExpressionBuilder()
                .in("fingerprint", "fingerprint-1")
                .build();
        assertEquals(expected, result);
    }

    @Test
    void createsNonMatchingFilterWithoutCompletedActiveIndex() {
        // given
        when(documentRepository.findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE))
                .thenReturn(List.of());
        when(manifestService.getCompletedFingerprints(List.of()))
                .thenReturn(Set.of());

        // when
        Filter.Expression result = filterFactory.create();

        // then
        Filter.Expression expected = new FilterExpressionBuilder()
                .eq("fingerprint", "__no_active_safety_document__")
                .build();
        assertEquals(expected, result);
    }
}
