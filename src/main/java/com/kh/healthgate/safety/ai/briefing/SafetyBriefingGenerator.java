package com.kh.healthgate.safety.ai.briefing;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SafetyBriefingGenerator {
    private final ChatClient chatClient;
    private final ContextualQueryAugmenter queryAugmenter = ContextualQueryAugmenter.builder()
            .allowEmptyContext(true)
            .build();
    private final Advisor simpleLoggerAdvisor;

    public SafetyBriefingGenerator(
            Builder chatClientBuilder) {
        this.simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
                .build();
        this.chatClient = chatClientBuilder.build();
    }

    public String generateSafetyBriefing(
            String weatherContext,
            String retrievalQuery,
            List<Document> documents) {
        Query generationQuery = new Query(SafetyBriefingPrompts.briefingRequest(weatherContext, retrievalQuery));
        Query augmentedQuery = documents.isEmpty()
                ? generationQuery
                : queryAugmenter.augment(generationQuery, documents);
        String instructions = documents.isEmpty()
                ? SafetyBriefingPrompts.BASIC_BRIEFING_INSTRUCTIONS
                : SafetyBriefingPrompts.BRIEFING_INSTRUCTIONS;
        String answer = chatClient
                .prompt()
                .advisors(simpleLoggerAdvisor)
                .system(instructions)
                .user(augmentedQuery.text())
                .call()
                .content();
        return answer;
    }

}
