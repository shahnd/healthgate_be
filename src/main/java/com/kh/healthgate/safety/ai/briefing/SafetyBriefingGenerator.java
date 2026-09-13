package com.kh.healthgate.safety.ai.briefing;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SafetyBriefingGenerator {
    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final ActiveSafetyDocumentFilterFactory filterFactory;
    private final Advisor simpleLoggerAdvisor;

    public SafetyBriefingGenerator(
            VectorStore vectorStore,
            ActiveSafetyDocumentFilterFactory filterFactory,
            Builder chatClientBuilder) {
        this.vectorStore = vectorStore;
        this.filterFactory = filterFactory;
        this.simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
                .build();
        this.chatClient = chatClientBuilder.build();
    }

    public String generateSafetyBriefing(
            String weatherContext,
            List<String> documentFingerprints) {
        Advisor retrievalAugmentationAdvisor = createRetrievalAdvisor(documentFingerprints);
        String answer = chatClient
                .prompt(SafetyBriefingPrompts.BRIEFING_INSTRUCTIONS)
                .advisors(retrievalAugmentationAdvisor)
                .advisors(simpleLoggerAdvisor)
                .user(u -> u.text(SafetyBriefingPrompts.WEATHER_REQUEST)
                        .param("weather-forecast", weatherContext))
                .call()
                .content();
        return answer;
    }

    private Advisor createRetrievalAdvisor(List<String> documentFingerprints) {
        return RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .filterExpression(filterFactory.create(documentFingerprints))
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
    }
}
