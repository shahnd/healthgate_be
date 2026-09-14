package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.LinkedHashMap;

import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.Filter;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SafetyBriefingDocumentRetriever {
    private static final String NO_MATCHING_FINGERPRINT = "__no_active_safety_document__";

    private final VectorStore vectorStore;

    public List<Document> retrieve(String retrievalQuery, List<String> documentFingerprints) {
        if (documentFingerprints.isEmpty()) {
            return List.of();
        }
        List<String> queries = retrievalQuery.lines()
                .map(line -> line.strip())
                .filter(line -> !line.isEmpty())
                .toList();
        if (queries.size() != 2) {
            log.warn("검색 쿼리가 두 줄이 아니므로 단일 검색을 수행합니다.");
            return retrieveSingle(retrievalQuery, documentFingerprints);
        }

        var documentsById = new LinkedHashMap<String, Document>();
        queries.stream().distinct().forEach(query -> {
            List<Document> documents = retrieveSingle(query, documentFingerprints);
            documents.forEach(document -> documentsById.putIfAbsent(document.getId(), document));
        });
        return List.copyOf(documentsById.values());
    }

    private List<Document> retrieveSingle(String retrievalQuery, List<String> documentFingerprints) {
        log.info("안전문서 검색 쿼리: {}", retrievalQuery);
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
