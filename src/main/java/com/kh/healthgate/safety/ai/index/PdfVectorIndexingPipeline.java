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
    private final VectorIndexManifestService manifestService;
    private final VectorIndexFingerprintFactory fingerprintFactory;

    public void index(Resource resource, String contentChecksum) {
        String sourceName = resource.getFilename();
        String fingerprint = fingerprintFactory.create(contentChecksum);

        if (manifestService.isCompleted(fingerprint)) {
            log.info("skip completed index: {}", sourceName);
            return;
        }

        manifestService.startIndexing(fingerprint, contentChecksum);
        try {
            vectorStore.delete(new FilterExpressionBuilder().eq("fingerprint", fingerprint).build());
            List<Document> documents = extract(resource).stream()
                    .map(document -> transform(document, fingerprint))
                    .toList();

            for (Document document : documents) {
                load(document);
            }

            manifestService.completeIndexing(fingerprint, documents.size());
        } catch (RuntimeException exception) {
            manifestService.failIndexing(fingerprint, exception.getMessage());
            throw exception;
        }
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
