package com.kh.healthgate.safety.ai.dao;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class VectorStoreRepository {
    private final VectorStore vectorStore;
    private final SearchRequest.Builder searchRequestBuilder = SearchRequest.builder()
            .query("noop")
            .topK(Integer.MAX_VALUE)
            .similarityThreshold(0.0);
    private final FilterExpressionBuilder b = new FilterExpressionBuilder();

    public Optional<Document> findById(UUID id) {
        List<Document> documents = vectorStore.similaritySearch(searchRequestBuilder
                .filterExpression(b.eq("id", id.toString()).build())
                .build());

        if (documents.isEmpty()) {
            return Optional.empty();
        }

        return Optional.of(documents.getFirst());
    }

    public List<Document> findAll() {
        return vectorStore.similaritySearch(searchRequestBuilder.build());
    }

    public List<Document> findByFileName(String fileName) {
        return vectorStore.similaritySearch(searchRequestBuilder
                .filterExpression(b.eq("file_name", fileName).build())
                .build());
    }

    public boolean existsByFileName(String name) {
        return !findByFileName(name).isEmpty();
    }

    public int count() {
        return findAll().size();
    }

    public int countByFileName(String fileName) {
        return findByFileName(fileName).size();
    }
}
