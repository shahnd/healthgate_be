package com.kh.healthgate.safety.ai.briefing;

import java.util.List;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.ChatClient.Builder;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class SafetyBriefingGenerator {
    private final ChatClient chatClient;
    private final SafetyBriefingRetrievalAdvisorFactory retrievalAdvisorFactory;
    private final Advisor simpleLoggerAdvisor;

    public SafetyBriefingGenerator(
            SafetyBriefingRetrievalAdvisorFactory retrievalAdvisorFactory,
            Builder chatClientBuilder) {
        this.retrievalAdvisorFactory = retrievalAdvisorFactory;
        this.simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
                .build();
        this.chatClient = chatClientBuilder.build();
    }

    public String generateSafetyBriefing(
            String weatherContext,
            List<String> documentFingerprints) {
        Advisor retrievalAugmentationAdvisor = retrievalAdvisorFactory.create(documentFingerprints);
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

}
