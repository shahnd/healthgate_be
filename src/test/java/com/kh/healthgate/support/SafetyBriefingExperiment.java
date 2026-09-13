package com.kh.healthgate.support;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.DriverManager;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.genai.Client;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.google.genai.GoogleGenAiChatModel;
import org.springframework.ai.google.genai.GoogleGenAiChatOptions;
import org.springframework.ai.google.genai.embedding.GoogleGenAiEmbeddingConnectionDetails;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingModel;
import org.springframework.ai.google.genai.text.GoogleGenAiTextEmbeddingOptions;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.support.ResourcePropertySource;

import com.kh.healthgate.opendata.weather.domain.*;
import com.kh.healthgate.safety.ai.briefing.*;
import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.config.AiProperties;
import com.kh.healthgate.safety.domain.SafetyDocument;

/** 실제 모델을 호출하는 수동 실험. 애플리케이션을 기동하거나 DB/벡터 데이터를 변경하지 않는다. */
public final class SafetyBriefingExperiment {
    private SafetyBriefingExperiment() {
    }

    public static void main(String[] args) throws Exception {
        String scenario = args.length == 0 ? "normal" : args[0];
        if (!List.of("normal", "hot", "empty", "all").contains(scenario)) {
            throw new IllegalArgumentException("시나리오: normal, hot, empty, all");
        }
        StandardEnvironment environment = new StandardEnvironment();
        environment.getPropertySources().addLast(new ResourcePropertySource("classpath:application.properties"));
        String apiKey = environment.getProperty("SPRING_AI_GOOGLE_GENAI_APIKEY",
                environment.getRequiredProperty("spring.ai.google.genai.api-key"));
        String model = environment.getRequiredProperty("spring.ai.google.genai.chat.model");
        Path output = Path.of("logs", "experiments", LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS")));
        Files.createDirectories(output);

        try (Client client = Client.builder().apiKey(apiKey).build()) {
            var chatModel = GoogleGenAiChatModel.builder().genAiClient(client)
                    .options(GoogleGenAiChatOptions.builder().model(model).build()).build();
            var embedding = new GoogleGenAiTextEmbeddingModel(
                    GoogleGenAiEmbeddingConnectionDetails.builder().genAiClient(client).build(),
                    GoogleGenAiTextEmbeddingOptions.builder().build());
            var properties = new AiProperties(
                    environment.getRequiredProperty("com.kh.healthgate.ai.vector-store-file-path"),
                    environment.getRequiredProperty("com.kh.healthgate.ai.pipeline-version"),
                    environment.getRequiredProperty("com.kh.healthgate.ai.embedding-model"));
            var fingerprintFactory = new VectorIndexFingerprintFactory(properties, embedding);
            List<SafetyDocument> catalog = scenario.equals("empty") ? List.of() : readDocuments(fingerprintFactory);
            var vectorStore = SimpleVectorStore.builder(embedding).build();
            if (!scenario.equals("empty")) {
                Path vectorFile = Path.of(properties.getVectorStoreFilePath());
                if (!Files.isRegularFile(vectorFile)) {
                    throw new IllegalStateException("벡터 파일이 없습니다: " + vectorFile);
                }
                vectorStore.load(vectorFile.toFile());
                if (catalog.isEmpty()) {
                    throw new IllegalStateException("활성·인덱싱 완료 문서가 없습니다. 문서를 인덱싱하거나 empty로 실행하세요.");
                }
            }
            var queryGenerator = new SafetyBriefingQueryGenerator(ChatClient.builder(chatModel));
            var retriever = new SafetyBriefingDocumentRetriever(vectorStore);
            var generator = new SafetyBriefingGenerator(ChatClient.builder(chatModel));
            for (String name : scenario.equals("all") ? List.of("normal", "hot", "empty") : List.of(scenario)) {
                String weather = System.getProperty("briefing.weather-file") == null
                        ? weather(name.equals("hot"))
                        : Files.readString(Path.of(System.getProperty("briefing.weather-file")));
                List<SafetyDocument> documents = name.equals("empty") ? List.of() : catalog;
                Path report = output.resolve(name + ".md");
                StringBuilder result = new StringBuilder("# " + name + "\n\n모델: " + model
                        + "\n\n## 기상 입력\n\n" + weather + "\n\n## 문서 목록\n\n");
                documents.forEach(document -> result.append("- ").append(document.getTitle()).append('\n'));
                Files.writeString(report, result);
                String query = queryGenerator.generate(weather, documents);
                result.append("\n## 검색 쿼리\n\n").append(query).append('\n');
                List<Document> retrieved = retriever.retrieve(query, documents.stream()
                        .map(document -> fingerprintFactory.create(document.getContentChecksum())).toList());
                result.append("\n## 검색 결과\n\n");
                retrieved.forEach(document -> result.append("- ").append(document.getId())
                        .append(" / score=").append(document.getScore())
                        .append(" / ").append(document.getMetadata()).append('\n'));
                result.append("\n## 검색 본문\n\n");
                retrieved.forEach(document -> result.append(document.getText()).append("\n\n"));
                Files.writeString(report, result);
                String answer = generator.generateSafetyBriefing(weather, retrieved);
                result.append("\n## 생성 결과\n\n").append(answer).append('\n');
                Files.writeString(report, result);
                System.out.println(name + ": " + retrieved.size() + "개 검색 결과, " + report.toAbsolutePath());
            }
        }
    }

    private static List<SafetyDocument> readDocuments(VectorIndexFingerprintFactory fingerprintFactory) throws Exception {
        List<SafetyDocument> documents = new ArrayList<>();
        try (var connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/healthgate_db", "root", "mysql")) {
            connection.setReadOnly(true);
            try (var statement = connection.createStatement(); var rows = statement.executeQuery("""
                    SELECT d.title, d.description, d.original_filename, d.content_checksum, m.fingerprint
                    FROM safety_documents d
                    JOIN vector_index_manifests m ON m.content_checksum = d.content_checksum
                    WHERE d.status = 'ACTIVE' AND m.status = 'COMPLETED'
                    ORDER BY d.id
                    """)) {
                while (rows.next()) {
                    String checksum = rows.getString("content_checksum");
                    if (fingerprintFactory.create(checksum).equals(rows.getString("fingerprint"))) {
                        documents.add(new SafetyDocument(rows.getString("title"), rows.getString("description"),
                                rows.getString("original_filename"), "manual-experiment", "application/pdf", 0L,
                                checksum, null));
                    }
                }
            }
        }
        return documents;
    }

    private static String weather(boolean hot) {
        // 비교 가능한 합성 예보. 실제 DB의 예보는 수정하지 않는다.
        return IntStream.rangeClosed(9, 18).mapToObj(hour -> new WeatherForecast(
                LocalDate.of(2026, 9, 14).atTime(hour, 0),
                new BigDecimal(hot ? "35" : "24"), new BigDecimal(hot ? "60" : "50"),
                BigDecimal.ZERO, "강수없음", "적설없음", new BigDecimal("2"),
                WeatherForecastPrecipitationType.NONE, WeatherForecastSkyCondition.CLEAR,
                WeatherForecastLocation.YEOKSAM1))
                .map(forecast -> WeatherContextFormatter.toWeatherContextLine.apply(forecast))
                .collect(Collectors.joining("\n"));
    }
}
