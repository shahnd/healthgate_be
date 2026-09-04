package com.kh.healthgate.safety.ai.index;

import java.util.List;
import java.util.Map;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.genai.errors.ClientException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class PdfVectorIndexingPipeline {
    private final VectorStore vectorStore;

    public int index(Resource resource, String fingerprint) {
        vectorStore.delete(new FilterExpressionBuilder().eq("fingerprint", fingerprint).build());
        List<Document> documents = extract(resource).stream()
                .map(document -> transform(document, fingerprint))
                .toList();

        for (Document document : documents) {
            load(document);
        }

        return documents.size();
    }

    private List<Document> extract(Resource resource) {
        DocumentReader reader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());
        return reader.read();
    }

    private Document transform(Document document, String fingerprint) {
        Map<String, Object> metadata = document.getMetadata();
        metadata.put("fingerprint", fingerprint);
        return new Document(
                document.getId(),
                document.getText().strip().replaceAll("\\s+", " "),
                metadata);
    }

    private void load(Document document) {
        while (true) {
            try {
                vectorStore.add(List.of(document));
                return;
            } catch (ClientException ex) {
                if (ex.code() != 429) {
                    throw ex;
                }
                log.warn("429 Too Many Requests: {}", ex.getMessage());
                waitForRateLimit();
            }
        }
    }

    private void waitForRateLimit() {
        try {
            Thread.sleep(60_000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("벡터 인덱싱이 중단되었습니다.", e);
        }
    }

}
