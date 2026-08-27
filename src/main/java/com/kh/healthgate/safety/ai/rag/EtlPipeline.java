package com.kh.healthgate.safety.ai.rag;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.google.genai.errors.ClientException;
import com.kh.healthgate.safety.ai.config.AiProperties;
import com.kh.healthgate.safety.ai.model.service.VectorIndexManifestService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtlPipeline {
    private final VectorStore vectorStore;
    private final VectorIndexManifestService manifestService;
    private final AiProperties properties;
    private final EmbeddingModel embeddingModel;

    public void index(Resource resource) {
        String sourceName = resource.getFilename();
        String fingerprint = fingerprint(resource);

        if (manifestService.isCompleted(fingerprint)) {
            log.info("skip completed index: {}", sourceName);
            return;
        }

        List<Document> documents = extract(resource).stream()
                .map(document -> transform(document, fingerprint))
                .toList();

        vectorStore.delete(new FilterExpressionBuilder().eq("fingerprint", fingerprint).build());
        manifestService.startIndexing(fingerprint, sourceName);

        for (Document document : documents) {
            load(document);
        }

        manifestService.completeIndexing(fingerprint);
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

    private String fingerprint(Resource resource) {
        String contentHash;
        try (InputStream inputStream = resource.getInputStream()) {
            contentHash = DigestUtils.sha512Hex(inputStream);
        } catch (IOException e) {
            throw new IllegalStateException("fingerprint 생성에 실패했습니다: " + resource.getFilename(), e);
        }

        String input = String.join("\n",
                "content-sha512=" + contentHash,
                "pipeline-version=" + properties.getPipelineVersion(),
                "embedding-model=" + properties.getEmbeddingModel(),
                "embedding-dimensions=" + embeddingModel.dimensions());
        return DigestUtils.sha512Hex(input);
    }
}
