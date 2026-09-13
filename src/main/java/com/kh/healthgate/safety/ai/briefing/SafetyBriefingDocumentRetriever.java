package com.kh.healthgate.safety.ai.briefing;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SafetyBriefingDocumentRetriever {
    private static final String NO_MATCHING_FINGERPRINT = "__no_active_safety_document__";

    private final VectorStore vectorStore;

    public List<Document> retrieve(String retrievalQuery, List<String> documentFingerprints) {
        return VectorStoreDocumentRetriever.builder()
                .similarityThreshold(0.50)
                .vectorStore(vectorStore)
                .filterExpression(createFilter(documentFingerprints))
                .build()
                .retrieve(new Query(retrievalQuery));
    }

    Filter.Expression createFilter(List<String> fingerprints) {
        FilterExpressionBuilder builder = new FilterExpressionBuilder();
        if (fingerprints.isEmpty()) {
            return builder.eq("fingerprint", NO_MATCHING_FINGERPRINT).build();
        }
        return builder.in("fingerprint", fingerprints.toArray()).build();
    }
}
