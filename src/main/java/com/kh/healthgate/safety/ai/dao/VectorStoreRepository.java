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
    private final SearchRequest.Builder searchRequestBuilder = SearchRequest.builder() // 모든 행을 매칭하는 SearchRequest 빌더
            .query("noop") // 빈 문자열이나 null 입력 시 Exception 발생함
            .topK(Integer.MAX_VALUE) // topK 미설정 시 행 누락 발생함
            .similarityThreshold(0.0); // threshold를 0.0으로 설정하여 모든 행 매칭
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

    public boolean existsByFileName(String fileName) {
        return !findByFileName(fileName).isEmpty();
    }

    public int count() {
        return findAll().size();
    }

    public int countByFileName(String fileName) {
        return findByFileName(fileName).size();
    }
}
