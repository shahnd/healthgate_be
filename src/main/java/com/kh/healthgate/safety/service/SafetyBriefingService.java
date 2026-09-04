package com.kh.healthgate.safety.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import org.springframework.stereotype.Service;

import com.kh.healthgate.opendata.weather.service.WeatherService;
import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.safety.ai.briefing.ActiveIndexedSafetyDocuments;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingGenerator;
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
    private final ActiveIndexedSafetyDocuments activeIndexedSafetyDocuments;
    private final WeatherService weatherService;
    private final SafetyBriefingRepository safetyBriefingRepository;

    public SafetyBriefingResponse getTodayBriefing() {
        LocalDate briefingDate = LocalDate.now(SEOUL);
        WeatherForecastLocation location = WeatherForecastLocation.YEOKSAM1;
        List<WeatherForecast> forecasts = weatherService
                .findBusinessHoursForecasts(briefingDate, location);
        List<String> documentFingerprints = activeIndexedSafetyDocuments.getFingerprints();
        SafetyBriefingContext context = SafetyBriefingContext.of(
                briefingDate,
                location,
                forecasts,
                documentFingerprints);
        String contextFingerprint = context.fingerprint();

        return safetyBriefingRepository
                .findByBriefingDateAndContextFingerprint(briefingDate, contextFingerprint)
                .map(SafetyBriefingResponse::from)
                .orElseGet(() -> createBriefing(context, contextFingerprint));
    }

    private SafetyBriefingResponse createBriefing(
            SafetyBriefingContext context,
            String contextFingerprint) {
        String content;
        try {
            content = generator.generateSafetyBriefing(
                    context.weatherContext(),
                    context.documentFingerprints());
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
