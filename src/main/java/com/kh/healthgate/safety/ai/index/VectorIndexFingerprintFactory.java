package com.kh.healthgate.safety.ai.index;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.ai.config.AiProperties;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class VectorIndexFingerprintFactory {
    private final AiProperties properties;
    private final EmbeddingModel embeddingModel;

    public String create(String contentChecksum) {
        String input = String.join("\n",
                "content-sha512=" + contentChecksum,
                "pipeline-version=" + properties.getPipelineVersion(),
                "embedding-model=" + properties.getEmbeddingModel(),
                "embedding-dimensions=" + embeddingModel.dimensions());
        return DigestUtils.sha512Hex(input);
    }
}
