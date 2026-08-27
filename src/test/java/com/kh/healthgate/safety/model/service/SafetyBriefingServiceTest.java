package com.kh.healthgate.safety.model.service;

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

import com.kh.healthgate.opendata.weather.model.service.WeatherService;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastPrecipitationType;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastSkyCondition;
import com.kh.healthgate.safety.ai.rag.SafetyBriefingGenerator;
import com.kh.healthgate.safety.model.dao.SafetyBriefingRepository;
import com.kh.healthgate.safety.model.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.model.vo.SafetyBriefing;
import com.kh.healthgate.safety.model.vo.SafetyBriefingContext;

@ExtendWith(MockitoExtension.class)
class SafetyBriefingServiceTest {

    @Mock
    private SafetyBriefingGenerator generator;
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
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts);
        SafetyBriefing cached = new SafetyBriefing(today, context.fingerprint(), "캐시된 브리핑");

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
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
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts);

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(today, context.fingerprint()))
                .thenReturn(Optional.empty());
        when(generator.generateSafetyBriefing(context.weatherContext())).thenReturn("새 브리핑");
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
    }

    private WeatherForecast forecastAt(LocalDateTime forecastAt) {
        return new WeatherForecast(
                forecastAt,
                new BigDecimal("27"),
                new BigDecimal("70"),
                new BigDecimal("20"),
                "강수없음",
                new BigDecimal("0.0"),
                new BigDecimal("1.5"),
                WeatherForecastPrecipitationType.NONE,
                WeatherForecastSkyCondition.CLEAR,
                WeatherForecastLocation.YEOKSAM1);
    }
}
