package com.kh.healthgate.safety.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastPrecipitationType;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastSkyCondition;

class SafetyBriefingContextTest {

    @Test
    void createsSameFingerprintRegardlessOfForecastOrder() {
        // given
        LocalDate briefingDate = LocalDate.of(2026, 8, 27);
        WeatherForecast morning = forecastAt(briefingDate.atTime(9, 0), "24");
        WeatherForecast afternoon = forecastAt(briefingDate.atTime(15, 0), "30");

        // when
        SafetyBriefingContext ordered = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                List.of(morning, afternoon),
                List.of("fingerprint-b", "fingerprint-a"));
        SafetyBriefingContext reversed = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                List.of(afternoon, morning),
                List.of("fingerprint-a", "fingerprint-b"));

        // then
        assertThat(reversed.weatherContext()).isEqualTo(ordered.weatherContext());
        assertThat(reversed.fingerprint()).isEqualTo(ordered.fingerprint());
        assertThat(ordered.fingerprint()).hasSize(64);
    }

    @Test
    void createsDifferentFingerprintWhenDocumentSetChanges() {
        // given
        LocalDate briefingDate = LocalDate.of(2026, 8, 27);
        List<WeatherForecast> forecasts = List.of(
                forecastAt(briefingDate.atTime(9, 0), "24"));

        // when
        SafetyBriefingContext first = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                List.of("fingerprint-a"));
        SafetyBriefingContext second = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                List.of("fingerprint-a", "fingerprint-b"));

        // then
        assertThat(second.fingerprint()).isNotEqualTo(first.fingerprint());
    }

    private WeatherForecast forecastAt(LocalDateTime forecastAt, String temperature) {
        return new WeatherForecast(
                forecastAt,
                new BigDecimal(temperature),
                new BigDecimal("70"),
                new BigDecimal("20"),
                "강수없음",
                "적설없음",
                new BigDecimal("1.5"),
                WeatherForecastPrecipitationType.NONE,
                WeatherForecastSkyCondition.CLEAR,
                WeatherForecastLocation.YEOKSAM1);
    }
}
