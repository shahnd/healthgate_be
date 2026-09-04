package com.kh.healthgate.safety.ai.briefing;

import java.util.Collection;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActiveSafetyDocumentFilterFactory {
    private static final String NO_MATCHING_FINGERPRINT = "__no_active_safety_document__";

    public Filter.Expression create(Collection<String> fingerprints) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        if (fingerprints.isEmpty()) {
            return builder.eq("fingerprint", NO_MATCHING_FINGERPRINT).build();
        }
        return builder.in("fingerprint", fingerprints.toArray()).build();
    }
}
