package com.kh.healthgate.safety.ai.rag;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.springframework.ai.document.Document;
import org.springframework.ai.document.DocumentReader;
import org.springframework.ai.document.DocumentTransformer;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.ai.dao.VectorStoreRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EtlPipeline {
    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;
    private Resource resource;
    private List<Document> documents;

    public EtlPipeline extract(Resource resource) {
        DocumentReader reader = new PagePdfDocumentReader(resource, PdfDocumentReaderConfig.builder()
                .withPageTopMargin(0)
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                .withPagesPerDocument(1)
                .build());

        this.resource = resource;
        this.documents = reader.read();
        return this;
    }

    public EtlPipeline transform() {
        DocumentTransformer transformer = TokenTextSplitter.builder()
                .build();

        Function<Document, Document> normalizeSpace = document -> new Document(
                document.getId(),
                document.getText().strip().replaceAll("\\s+", " "),
                document.getMetadata());

        Function<Document, Document> addIdToMetadata = document -> {
            Map<String, Object> metadata = document.getMetadata();
            metadata.put("id", document.getId());
            return new Document(
                    document.getId(),
                    document.getText(),
                    metadata);
        };

        this.documents = transformer.apply(documents).stream()
                .map(normalizeSpace)
                .map(addIdToMetadata)
                .toList();
        return this;
    }

    public List<Document> load() {
        String fileName = resource.getFilename();

        if (vectorStoreRepository.existsByFileName(fileName)) {
            throw new RuntimeException("이미 인덱싱된 파일입니다: " + fileName);
        }

        vectorStore.add(documents);
        return documents;
    }

    @Component
    @RequiredArgsConstructor
    public class EtlPipelineFactory {
        private final VectorStore vectorStore;
        private final VectorStoreRepository vectorStoreRepository;

        public EtlPipeline extract(Resource resource) {
            return new EtlPipeline(vectorStore, vectorStoreRepository).extract(resource);
        }
    }
}
