package com.kh.healthgate.safety.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.ai.document.Document;

import com.kh.healthgate.opendata.weather.service.WeatherService;
import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingGenerator;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingDocumentRetriever;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingQueryGenerator;
import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.exception.SafetyBriefingGenerationException;
import com.kh.healthgate.safety.repository.SafetyBriefingRepository;
import com.kh.healthgate.safety.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.domain.SafetyBriefing;
import com.kh.healthgate.safety.domain.SafetyBriefingContext;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SafetyBriefingService {
    private static final ZoneId SEOUL = ZoneId.of("Asia/Seoul");

    private final SafetyBriefingGenerator generator;
    private final SafetyBriefingQueryGenerator queryGenerator;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final SafetyBriefingDocumentRetriever documentRetriever;
    private final SearchableSafetyDocumentService searchableSafetyDocumentService;
    private final WeatherService weatherService;
    private final SafetyBriefingRepository safetyBriefingRepository;

    public SafetyBriefingResponse getTodayBriefing() {
        LocalDate briefingDate = LocalDate.now(SEOUL);
        WeatherForecastLocation location = WeatherForecastLocation.YEOKSAM1;
        List<WeatherForecast> forecasts = weatherService
                .findBusinessHoursForecasts(briefingDate, location);
        List<SafetyDocument> searchableDocuments = searchableSafetyDocumentService.findDocuments();
        List<String> documentFingerprints = searchableDocuments.stream()
                .map(document -> fingerprintFactory.create(document.getContentChecksum()))
                .toList();
        SafetyBriefingContext context = SafetyBriefingContext.of(
                briefingDate,
                location,
                forecasts,
                documentFingerprints,
                searchableDocuments);
        String contextFingerprint = context.fingerprint();

        return safetyBriefingRepository
                .findByBriefingDateAndContextFingerprint(briefingDate, contextFingerprint)
                .map(SafetyBriefingResponse::from)
                .orElseGet(() -> createBriefing(context, contextFingerprint, searchableDocuments));
    }

    private SafetyBriefingResponse createBriefing(
            SafetyBriefingContext context,
            String contextFingerprint,
            List<SafetyDocument> searchableDocuments) {
        String content;
        try {
            String retrievalQuery = queryGenerator.generate(context.weatherContext(), searchableDocuments);
            List<Document> documents = documentRetriever.retrieve(retrievalQuery, context.documentFingerprints());
            content = generator.generateSafetyBriefing(
                    context.weatherContext(),
                    documents);
        } catch (RuntimeException exception) {
            throw new SafetyBriefingGenerationException(exception);
        }
        SafetyBriefing briefing = safetyBriefingRepository.save(new SafetyBriefing(
                context.briefingDate(),
                contextFingerprint,
                content));

        return SafetyBriefingResponse.from(briefing);
    }
}
