package com.kh.healthgate.safety.ai.index;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.domain.SafetyDocumentIndexingRequest;

@Component
public class SafetyDocumentChunker {
    public List<Document> chunk(List<Document> pages, SafetyDocumentIndexingRequest request) {
        return pages.stream()
                .map(page -> transform(page, request.fingerprint()))
                .toList();
    }

    private Document transform(Document page, String fingerprint) {
        Map<String, Object> metadata = page.getMetadata();
        metadata.put("fingerprint", fingerprint);
        return new Document(
                page.getId(),
                page.getText().strip().replaceAll("\\s+", " "),
                metadata);
    }
}
