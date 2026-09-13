package com.kh.healthgate.safety.ai.index;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.springframework.ai.document.Document;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.domain.SafetyDocumentIndexingRequest;

@Component
public class SafetyDocumentChunker {
    public List<Document> chunk(List<Document> pages, SafetyDocumentIndexingRequest request) {
        return IntStream.range(0, pages.size())
                .mapToObj(index -> transform(pages.get(index), request, index))
                .toList();
    }

    private Document transform(Document page, SafetyDocumentIndexingRequest request, int chunkIndex) {
        Map<String, Object> metadata = new HashMap<>(page.getMetadata());
        metadata.put("fingerprint", request.fingerprint());
        metadata.put("title", request.title());
        metadata.put("original_filename", request.originalFilename());
        metadata.put("chunk_index", chunkIndex);
        return new Document(page.getId(), normalizeText(page.getText()), metadata);
    }

    private String normalizeText(String text) {
        return text.replaceAll("\\R", "\n")
                .replaceAll("[\\p{Cc}&&[^\\n\\t]]", "")
                .replaceAll("\\h+", " ")
                .replaceAll("(?m)^ +| +$", "")
                .strip();
    }
}
