package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;

final class SafetyBriefingPromptFormatter {
    private SafetyBriefingPromptFormatter() {
    }

    static String briefingRequest(String weatherContext) {
        return new PromptTemplate(SafetyBriefingPrompts.BRIEFING_REQUEST)
                .render(Map.of("weather-forecast", weatherContext));
    }

    static String documentContext(List<Document> documents) {
        return IntStream.range(0, documents.size()).mapToObj(index -> {
            Document document = documents.get(index);
            return SafetyBriefingPrompts.DOCUMENT_ENTRY.formatted(index + 1,
                    document.getMetadata().getOrDefault("title", "제목 미확인"),
                    document.getMetadata().getOrDefault("page_number", "페이지 미확인"),
                    document.getText(), index + 1);
        }).collect(Collectors.joining("\n"));
    }
}
