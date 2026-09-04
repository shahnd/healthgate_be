package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.Set;

import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveSafetyDocumentFilterFactory {
    private static final String NO_MATCHING_FINGERPRINT = "__no_active_safety_document__";

    private final SafetyDocumentRepository documentRepository;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final VectorIndexManifestService manifestService;

    public Filter.Expression create() {
        List<String> fingerprints = documentRepository
                .findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE)
                .stream()
                .map(fingerprintFactory::create)
                .toList();
        Set<String> completedFingerprints = manifestService
                .getCompletedFingerprints(fingerprints);

        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        if (completedFingerprints.isEmpty()) {
            return builder.eq("fingerprint", NO_MATCHING_FINGERPRINT).build();
        }
        return builder.in("fingerprint", completedFingerprints.toArray()).build();
    }
}
