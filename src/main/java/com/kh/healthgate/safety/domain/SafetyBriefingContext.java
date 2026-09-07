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
        List<String> documentFingerprints) {

    private static final String CONTEXT_VERSION = "v2";

    public SafetyBriefingContext {
        documentFingerprints = documentFingerprints.stream()
                .sorted()
                .toList();
    }

    public static SafetyBriefingContext of(
            LocalDate briefingDate,
            WeatherForecastLocation location,
            List<WeatherForecast> forecasts,
            List<String> documentFingerprints) {
        String weatherContext = forecasts.stream()
                .sorted((left, right) -> left.getForecastAt().compareTo(right.getForecastAt()))
                .map(WeatherContextFormatter.toWeatherContextLine)
                .collect(Collectors.joining("\n"));

        return new SafetyBriefingContext(
                briefingDate,
                location,
                weatherContext,
                documentFingerprints);
    }

    public String fingerprint() {
        String normalizedContext = String.join(
                "\n",
                "version=" + CONTEXT_VERSION,
                "location=" + location.name(),
                weatherContext,
                documentFingerprints.stream()
                        .map(fingerprint -> "document=" + fingerprint)
                        .collect(Collectors.joining("\n")));

        return DigestUtils.sha256Hex(normalizedContext);
    }
}
