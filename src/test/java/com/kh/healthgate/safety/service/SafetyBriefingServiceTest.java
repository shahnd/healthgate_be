package com.kh.healthgate.safety.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kh.healthgate.opendata.weather.service.WeatherService;
import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastPrecipitationType;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastSkyCondition;
import com.kh.healthgate.safety.ai.briefing.ActiveIndexedSafetyDocuments;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingGenerator;
import com.kh.healthgate.safety.repository.SafetyBriefingRepository;
import com.kh.healthgate.safety.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.domain.SafetyBriefing;
import com.kh.healthgate.safety.domain.SafetyBriefingContext;

@ExtendWith(MockitoExtension.class)
class SafetyBriefingServiceTest {

    @Mock
    private SafetyBriefingGenerator generator;
    @Mock
    private ActiveIndexedSafetyDocuments activeIndexedSafetyDocuments;
    @Mock
    private WeatherService weatherService;
    @Mock
    private SafetyBriefingRepository safetyBriefingRepository;

    @InjectMocks
    private SafetyBriefingService safetyBriefingService;

    @Test
    void returnsCachedBriefingWithoutGeneratingAgain() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<WeatherForecast> forecasts = List.of(forecastAt(today.atTime(9, 0)));
        List<String> documentFingerprints = List.of("fingerprint-a");
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                documentFingerprints);
        SafetyBriefing cached = new SafetyBriefing(today, context.fingerprint(), "캐시된 브리핑");

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
        when(activeIndexedSafetyDocuments.getFingerprints()).thenReturn(documentFingerprints);
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(today, context.fingerprint()))
                .thenReturn(Optional.of(cached));

        // when
        SafetyBriefingResponse response = safetyBriefingService.getTodayBriefing();

        // then
        assertThat(response.briefingDate()).isEqualTo(today);
        assertThat(response.content()).isEqualTo("캐시된 브리핑");
        verifyNoInteractions(generator);
        verify(safetyBriefingRepository, never()).save(any());
    }

    @Test
    void generatesAndCachesBriefingOnCacheMiss() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<WeatherForecast> forecasts = List.of(forecastAt(today.atTime(9, 0)));
        List<String> documentFingerprints = List.of("fingerprint-a");
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                documentFingerprints);

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
        when(activeIndexedSafetyDocuments.getFingerprints()).thenReturn(documentFingerprints);
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(today, context.fingerprint()))
                .thenReturn(Optional.empty());
        when(generator.generateSafetyBriefing(
                context.weatherContext(),
                documentFingerprints)).thenReturn("새 브리핑");
        when(safetyBriefingRepository.save(any(SafetyBriefing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SafetyBriefingResponse response = safetyBriefingService.getTodayBriefing();

        // then
        assertThat(response.briefingDate()).isEqualTo(today);
        assertThat(response.content()).isEqualTo("새 브리핑");

        ArgumentCaptor<SafetyBriefing> briefingCaptor = ArgumentCaptor.forClass(SafetyBriefing.class);
        verify(safetyBriefingRepository).save(briefingCaptor.capture());
        assertThat(briefingCaptor.getValue().getContextFingerprint()).isEqualTo(context.fingerprint());
        verify(generator).generateSafetyBriefing(
                context.weatherContext(),
                documentFingerprints);
    }

    private WeatherForecast forecastAt(LocalDateTime forecastAt) {
        return new WeatherForecast(
                forecastAt,
                new BigDecimal("27"),
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
