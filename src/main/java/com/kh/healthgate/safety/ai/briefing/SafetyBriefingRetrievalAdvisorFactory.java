package com.kh.healthgate.safety.ai.briefing;

import java.util.List;

import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SafetyBriefingRetrievalAdvisorFactory {
    private static final String NO_MATCHING_FINGERPRINT = "__no_active_safety_document__";

    private final VectorStore vectorStore;

    public Advisor create(List<String> documentFingerprints) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .filterExpression(createFilter(documentFingerprints))
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
    }

    Filter.Expression createFilter(List<String> fingerprints) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        if (fingerprints.isEmpty()) {
            return builder.eq("fingerprint", NO_MATCHING_FINGERPRINT).build();
        }
        return builder.in("fingerprint", fingerprints.toArray()).build();
    }
}
