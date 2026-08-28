package com.kh.healthgate.safety.ai.config;

import java.io.File;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class VectorStoreConfig {
    private final AiProperties properties;
    private final EmbeddingModel embeddingModel;

    @Bean
    VectorStore vectorStore() {
        File file = new File(properties.getVectorStoreFilePath());
        VectorStore vectorStore = new PersistentSimpleVectorStore(embeddingModel, file);

        return vectorStore;
    }

    /**
     * PersistentSimpleVectorStore
     * 개발용 임시 VectorStore
     */
    private class PersistentSimpleVectorStore extends SimpleVectorStore {
        private final File file;

        private PersistentSimpleVectorStore(
                EmbeddingModel embeddingModel,
                File file) {
            super(SimpleVectorStore.builder(embeddingModel));
            this.file = file;

            if (file.exists()) {
                load(file);
            }
        }

        @Override
        public void doAdd(List<Document> documents) {
            super.doAdd(documents);
            save(file);
        }

        @Override
        public void doDelete(List<String> idList) {
            super.doDelete(idList);
            save(file);
        }
    }
}
