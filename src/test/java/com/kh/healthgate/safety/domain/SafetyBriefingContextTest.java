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
                List.of("fingerprint-b", "fingerprint-a"),
                List.of());
        SafetyBriefingContext reversed = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                List.of(afternoon, morning),
                List.of("fingerprint-a", "fingerprint-b"),
                List.of());

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
                List.of("fingerprint-a"),
                List.of());
        SafetyBriefingContext second = SafetyBriefingContext.of(
                briefingDate,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                List.of("fingerprint-a", "fingerprint-b"),
                List.of());

        // then
        assertThat(second.fingerprint()).isNotEqualTo(first.fingerprint());
    }

    @Test
    void createsSameFingerprintRegardlessOfDecimalScale() {
        LocalDate date = LocalDate.of(2026, 9, 14);
        WeatherForecast original = forecastAt(date.atTime(9, 0), "24");
        WeatherForecast persisted = new WeatherForecast(
                original.getForecastAt(), new BigDecimal("24.00"), new BigDecimal("70.00"),
                new BigDecimal("20.00"), "강수없음", "적설없음", new BigDecimal("1.50"),
                WeatherForecastPrecipitationType.NONE, WeatherForecastSkyCondition.CLEAR,
                WeatherForecastLocation.YEOKSAM1);
        SafetyBriefingContext first = SafetyBriefingContext.of(
                date, WeatherForecastLocation.YEOKSAM1, List.of(original), List.of(), List.of());
        SafetyBriefingContext second = SafetyBriefingContext.of(
                date, WeatherForecastLocation.YEOKSAM1, List.of(persisted), List.of(), List.of());
        SafetyBriefingContext changed = SafetyBriefingContext.of(
                date, WeatherForecastLocation.YEOKSAM1,
                List.of(forecastAt(date.atTime(9, 0), "24.01")), List.of(), List.of());

        assertThat(second.weatherContext()).isEqualTo(first.weatherContext());
        assertThat(second.fingerprint()).isEqualTo(first.fingerprint());
        assertThat(changed.fingerprint()).isNotEqualTo(first.fingerprint());
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
