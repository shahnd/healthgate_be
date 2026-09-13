package com.kh.healthgate.safety.ai.briefing;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

class SafetyBriefingRetrievalAdvisorFactoryTest {
    private final SafetyBriefingRetrievalAdvisorFactory advisorFactory =
            new SafetyBriefingRetrievalAdvisorFactory(mock(VectorStore.class));

    @Test
    void createsFilterForDocumentFingerprints() {
        // given
        List<String> fingerprints = List.of("fingerprint-1", "fingerprint-2");

        // when
        Filter.Expression result = advisorFactory.createFilter(fingerprints);

        // then
        Filter.Expression expected = new FilterExpressionBuilder()
                .in("fingerprint", "fingerprint-1", "fingerprint-2")
                .build();
        assertEquals(expected, result);
    }

    @Test
    void createsNonMatchingFilterWithoutDocumentFingerprint() {
        // given
        List<String> fingerprints = List.of();

        // when
        Filter.Expression result = advisorFactory.createFilter(fingerprints);

        // then
        Filter.Expression expected = new FilterExpressionBuilder()
                .eq("fingerprint", "__no_active_safety_document__")
                .build();
        assertEquals(expected, result);
    }
}
