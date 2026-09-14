package com.kh.healthgate.safety.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.codec.digest.DigestUtils;

import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.safety.ai.briefing.WeatherContextFormatter;

public record SafetyBriefingContext(
        LocalDate briefingDate,
        WeatherForecastLocation location,
        String weatherContext,
        List<String> documentFingerprints,
        String documentMetadata) {

    private static final String CONTEXT_VERSION = "v14";

    public SafetyBriefingContext {
        documentFingerprints = documentFingerprints.stream()
                .sorted()
                .toList();
    }

    public static SafetyBriefingContext of(
            LocalDate briefingDate,
            WeatherForecastLocation location,
            List<WeatherForecast> forecasts,
            List<String> documentFingerprints,
            List<SafetyDocument> searchableDocuments) {
        String weatherContext = forecasts.stream()
                .sorted((left, right) -> left.getForecastAt().compareTo(right.getForecastAt()))
                .map(WeatherContextFormatter.toWeatherContextLine)
                .collect(Collectors.joining("\n"));

        StringBuilder documentMetadata = new StringBuilder();
        for (SafetyDocument document : searchableDocuments) {
            appendField(documentMetadata, document.getTitle());
            appendField(documentMetadata, document.getDescription());
            appendField(documentMetadata, document.getOriginalFilename());
        }

        return new SafetyBriefingContext(
                briefingDate,
                location,
                weatherContext,
                documentFingerprints,
                documentMetadata.toString());
    }

    public String fingerprint() {
        String normalizedContext = String.join(
                "\n",
                "version=" + CONTEXT_VERSION,
                "location=" + location.name(),
                weatherContext,
                documentFingerprints.stream()
                        .map(fingerprint -> "document=" + fingerprint)
                        .collect(Collectors.joining("\n")),
                documentMetadata);

        return DigestUtils.sha256Hex(normalizedContext);
    }

    private static void appendField(StringBuilder input, String value) {
        String normalized = value == null ? "" : value;
        input.append('|').append(normalized.length()).append(':').append(normalized);
    }
}
