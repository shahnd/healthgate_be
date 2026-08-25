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
import org.springframework.ai.vectorstore.filter.FilterExpressionBuilder;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.ai.dao.VectorStoreRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class EtlPipeline {
    private final VectorStore vectorStore;
    private final VectorStoreRepository vectorStoreRepository;
    private final FilterExpressionBuilder b = new FilterExpressionBuilder();

    private Resource resource;
    private List<Document> documents;

    private final Function<Document, Document> normalizeSpace = document -> new Document(
            document.getId(),
            document.getText().strip().replaceAll("\\s+", " "),
            document.getMetadata());

    private final Function<Document, Document> addIdToMetadata = document -> {
        Map<String, Object> metadata = document.getMetadata();
        metadata.put("id", document.getId());
        return new Document(
                document.getId(),
                document.getText(),
                metadata);
    };

    private static final String SOURCE_CHUNK_COUNT = "source_chunk_count";
    private final Function<Document, Document> addSourceChunkCountToMetadata = document -> {
        Map<String, Object> metadata = document.getMetadata();
        metadata.put(SOURCE_CHUNK_COUNT, documents.size());
        return new Document(
                document.getId(),
                document.getText(),
                metadata);
    };

    private void clean(String fileName) {
        if (!vectorStoreRepository.existsByFileName(fileName)) {
            return;
        }

        List<Document> documents = vectorStoreRepository.findByFileName(fileName);
        int indexed = documents.size();
        int total = (int) documents.getFirst().getMetadata().get(SOURCE_CHUNK_COUNT);
        if (indexed != total) {
            log.warn("clean partially indexed document");
            vectorStore.delete(b.eq("file_name", fileName).build());
        }
    }

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

        this.documents = transformer.apply(documents).stream()
                .map(normalizeSpace)
                .map(addIdToMetadata)
                .map(addSourceChunkCountToMetadata)
                .toList();
        return this;
    }

    public List<Document> load() {
        String fileName = resource.getFilename();

        clean(fileName);

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
