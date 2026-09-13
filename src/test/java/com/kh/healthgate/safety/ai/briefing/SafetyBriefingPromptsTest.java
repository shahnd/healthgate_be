package com.kh.healthgate.safety.ai.briefing;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.Query;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;

class SafetyBriefingPromptsTest {
    @Test
    void includesSourceBoundariesBeforeWeatherAndFinalRequest() {
        var augmenter = ContextualQueryAugmenter.builder()
                .promptTemplate(new PromptTemplate(SafetyBriefingPrompts.DOCUMENT_CONTEXT))
                .documentFormatter(documents -> SafetyBriefingPrompts.documentContext(documents)).build();
        var documents = List.of(
                new Document("본문 A\n다음 줄", Map.of("title", "화재 예방", "page_number", 1)),
                new Document("본문 B", Map.of("title", "지게차", "page_number", 2)));

        String prompt = augmenter.augment(new Query(SafetyBriefingPrompts.briefingRequest("24°C")), documents).text();

        assertThat(prompt).contains("[문서 1]\n제목: 화재 예방\n페이지: 1", "본문 A\n다음 줄",
                "[문서 1 끝]", "[문서 2]\n제목: 지게차\n페이지: 2");
        assertThat(prompt.indexOf("[문서 2 끝]")).isLessThan(prompt.indexOf("근무시간 기상예보:"));
        assertThat(prompt.strip()).endsWith("금일 우리 회사 근로자들이 사용할 안전 브리핑을 생성해 줘.");
    }
}
