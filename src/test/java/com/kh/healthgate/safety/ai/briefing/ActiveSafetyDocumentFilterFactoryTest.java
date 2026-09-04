package com.kh.healthgate.safety.ai.briefing;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;

class ActiveSafetyDocumentFilterFactoryTest {
    private final ActiveSafetyDocumentFilterFactory filterFactory =
            new ActiveSafetyDocumentFilterFactory();

    @Test
    void createsFilterForDocumentFingerprints() {
        // given
        List<String> fingerprints = List.of("fingerprint-1", "fingerprint-2");

        // when
        Filter.Expression result = filterFactory.create(fingerprints);

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
        Filter.Expression result = filterFactory.create(fingerprints);

        // then
        Filter.Expression expected = new FilterExpressionBuilder()
                .eq("fingerprint", "__no_active_safety_document__")
                .build();
        assertEquals(expected, result);
    }
}
