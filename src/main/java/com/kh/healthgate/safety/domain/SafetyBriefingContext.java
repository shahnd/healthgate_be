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
        String weatherContext) {

    private static final String CONTEXT_VERSION = "v1";

    public static SafetyBriefingContext of(
            LocalDate briefingDate,
            WeatherForecastLocation location,
            List<WeatherForecast> forecasts) {
        String weatherContext = forecasts.stream()
                .sorted((left, right) -> left.getForecastAt().compareTo(right.getForecastAt()))
                .map(WeatherContextFormatter.toWeatherContextLine)
                .collect(Collectors.joining("\n"));

        return new SafetyBriefingContext(briefingDate, location, weatherContext);
    }

    public String fingerprint() {
        String normalizedContext = String.join(
                "\n",
                "version=" + CONTEXT_VERSION,
                "location=" + location.name(),
                weatherContext);

        return DigestUtils.sha256Hex(normalizedContext);
    }
}
