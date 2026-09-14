package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.stereotype.Component;

import com.kh.healthgate.safety.domain.SafetyDocument;

@Component
public class SafetyBriefingQueryGenerator {
    private final ChatClient chatClient;

    public SafetyBriefingQueryGenerator(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.clone()
                .defaultAdvisors(SimpleLoggerAdvisor.builder().build())
                .build();
    }

    public String generate(String weatherContext, List<SafetyDocument> searchableDocuments) {
        String query = chatClient.prompt()
                .system(SafetyBriefingPrompts.QUERY_INSTRUCTIONS)
                .user(user -> user.text(SafetyBriefingPrompts.QUERY_REQUEST)
                        .param("weather-forecast", weatherContext)
                        .param("documents", formatDocuments(searchableDocuments)))
                .call()
                .content();
        if (query == null || query.isBlank()) {
            throw new IllegalStateException("안전 브리핑 검색 쿼리가 비어 있습니다.");
        }
        return query.strip();
    }

    private String formatDocuments(List<SafetyDocument> documents) {
        if (documents.isEmpty()) {
            return "검색 가능한 문서 없음";
        }
        return documents.stream()
                .map(document -> "제목: %s\n설명: %s\n파일명: %s".formatted(
                        document.getTitle(),
                        document.getDescription() == null ? "" : document.getDescription(),
                        document.getOriginalFilename()))
                .collect(Collectors.joining("\n\n"));
    }
}
