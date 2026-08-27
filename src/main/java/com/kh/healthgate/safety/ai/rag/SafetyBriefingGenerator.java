package com.kh.healthgate.safety.ai.rag;

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
    private final Advisor retrievalAugmentationAdvisor;
    private final Advisor simpleLoggerAdvisor;

    public SafetyBriefingGenerator(
            VectorStore vectorStore,
            Builder chatClientBuilder) {
        this.retrievalAugmentationAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(VectorStoreDocumentRetriever.builder()
                        .similarityThreshold(0.50)
                        .vectorStore(vectorStore)
                        .build())
                .queryAugmenter(ContextualQueryAugmenter.builder()
                        .allowEmptyContext(true)
                        .build())
                .build();
        this.simpleLoggerAdvisor = SimpleLoggerAdvisor.builder()
                .build();
        this.chatClient = chatClientBuilder.build();
    }

    public String generateSafetyBriefing(String weatherContext) {
        String answer = chatClient
                .prompt(
                        """
                                당신은 물류센터 근로자를 위한 안전 브리핑을 작성하는 도우미입니다.

                                제공된 근무시간 기상예보와 context information을 바탕으로,
                                출근하는 근로자가 오늘 작업 중 주의해야 할 사항을 짧고 명확하게 작성하세요.

                                안전수칙 문장은 다음 조건을 준수하세요.
                                - '~하세요'로 종결하세요.
                                - 근로자의 권리를 설명하고 적극적으로 조치를 취할 수 있도록 독려하세요.

                                출력 형식:

                                [오늘의 안전 브리핑]

                                {기상예보 내의 특별히 주의해야 하는 내용을 1문장으로 설명합니다}
                                {기상예보 내의 특별히 주의해야 할 시간대가 존재한다면 추가로 1문장으로 설명합니다}

                                - {구체적인 안전수칙}
                                - {구체적인 안전수칙}
                                - {구체적인 안전수칙}
                                {필요한 경우 안전수칙을 더 작성합니다}
                                """)
                .advisors(retrievalAugmentationAdvisor)
                .advisors(simpleLoggerAdvisor)
                .user(u -> u.text(
                        """
                                근무시간 기상예보:
                                {weather-forecast}

                                금일 우리 회사 근로자들이 사용할 안전 브리핑을 생성해 줘.
                                """)
                        .param("weather-forecast", weatherContext))
                .call()
                .content();
        return answer;
    }
}
