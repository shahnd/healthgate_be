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
    private static final int MAX_RATE_LIMIT_RETRIES = 3;

    private final VectorStore vectorStore;
    private final VectorIndexManifestService manifestService;

    public int index(Resource resource, String fingerprint) {
        log.info("try indexing: {}", resource.getFilename());
        manifestService.heartbeat(fingerprint);
        vectorStore.delete(new FilterExpressionBuilder().eq("fingerprint", fingerprint).build());
        List<Document> documents = extract(resource).stream()
                .map(document -> transform(document, fingerprint))
                .toList();

        for (Document document : documents) {
            manifestService.heartbeat(fingerprint);
            load(document, fingerprint);
            manifestService.heartbeat(fingerprint);
        }

        log.info("{} successfully indexed");

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

    private void load(Document document, String fingerprint) {
        int retryCount = 0;

        while (true) {
            try {
                vectorStore.add(List.of(document));
                return;
            } catch (ClientException ex) {
                if (ex.code() != 429) {
                    throw ex;
                }

                if (retryCount >= MAX_RATE_LIMIT_RETRIES) {
                    log.error(
                            "429 재시도 한도를 초과했습니다. documentId={}, maxRetries={}",
                            document.getId(),
                            MAX_RATE_LIMIT_RETRIES);
                    throw ex;
                }

                retryCount++;
                log.warn(
                        "429 Too Many Requests: 재시도합니다. documentId={}, retry={}/{}, message={}",
                        document.getId(),
                        retryCount,
                        MAX_RATE_LIMIT_RETRIES,
                        ex.getMessage());
                manifestService.heartbeat(fingerprint);
                waitForRateLimit();
                manifestService.heartbeat(fingerprint);
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
