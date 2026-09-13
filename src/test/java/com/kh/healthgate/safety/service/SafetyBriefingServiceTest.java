package com.kh.healthgate.safety.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
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
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingGenerator;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingDocumentRetriever;
import com.kh.healthgate.safety.ai.briefing.SafetyBriefingQueryGenerator;
import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.repository.SafetyBriefingRepository;
import com.kh.healthgate.safety.dto.SafetyBriefingResponse;
import com.kh.healthgate.safety.domain.SafetyBriefing;
import com.kh.healthgate.safety.domain.SafetyBriefingContext;
import com.kh.healthgate.safety.exception.SafetyBriefingGenerationException;

@ExtendWith(MockitoExtension.class)
class SafetyBriefingServiceTest {

    @Mock
    private SafetyBriefingGenerator generator;
    @Mock
    private SafetyBriefingQueryGenerator queryGenerator;
    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;
    @Mock
    private SafetyBriefingDocumentRetriever documentRetriever;
    @Mock
    private SearchableSafetyDocumentService searchableSafetyDocumentService;
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
        List<SafetyDocument> searchableDocuments = List.of(new SafetyDocument(
                "안전수칙", null, "safety.pdf", "storage-key", "application/pdf", 1L, "checksum-a", null));
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                documentFingerprints,
                searchableDocuments);
        SafetyBriefing cached = new SafetyBriefing(today, context.fingerprint(), "캐시된 브리핑");

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
        when(searchableSafetyDocumentService.findDocuments()).thenReturn(searchableDocuments);
        when(fingerprintFactory.create("checksum-a")).thenReturn("fingerprint-a");
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(today, context.fingerprint()))
                .thenReturn(Optional.of(cached));

        // when
        SafetyBriefingResponse response = safetyBriefingService.getTodayBriefing();

        // then
        assertThat(response.briefingDate()).isEqualTo(today);
        assertThat(response.content()).isEqualTo("캐시된 브리핑");
        verifyNoInteractions(queryGenerator, generator, documentRetriever);
        verify(safetyBriefingRepository, never()).save(any());
    }

    @Test
    void generatesAndCachesBriefingOnCacheMiss() {
        // given
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Seoul"));
        List<WeatherForecast> forecasts = List.of(forecastAt(today.atTime(9, 0)));
        List<String> documentFingerprints = List.of("fingerprint-a");
        List<SafetyDocument> searchableDocuments = List.of(new SafetyDocument(
                "안전수칙", null, "safety.pdf", "storage-key", "application/pdf", 1L, "checksum-a", null));
        SafetyBriefingContext context = SafetyBriefingContext.of(
                today,
                WeatherForecastLocation.YEOKSAM1,
                forecasts,
                documentFingerprints,
                searchableDocuments);

        when(weatherService.findBusinessHoursForecasts(today, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(forecasts);
        when(searchableSafetyDocumentService.findDocuments()).thenReturn(searchableDocuments);
        when(fingerprintFactory.create("checksum-a")).thenReturn("fingerprint-a");
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(today, context.fingerprint()))
                .thenReturn(Optional.empty());
        List<Document> documents = List.of(new Document("안전수칙"));
        when(queryGenerator.generate(context.weatherContext(), searchableDocuments))
                .thenReturn("생성된 검색 쿼리");
        when(documentRetriever.retrieve(
                "생성된 검색 쿼리", documentFingerprints))
                .thenReturn(documents);
        when(generator.generateSafetyBriefing(
                context.weatherContext(),
                "생성된 검색 쿼리",
                documents)).thenReturn("새 브리핑");
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
                "생성된 검색 쿼리",
                documents);
    }

    @Test
    void concurrentRequestsGenerateAndSaveOnlyOnce() throws Exception {
        CountDownLatch generationStarted = new CountDownLatch(1);
        CountDownLatch releaseGeneration = new CountDownLatch(1);
        CountDownLatch cacheLookups = new CountDownLatch(3);
        AtomicReference<SafetyBriefing> cached = new AtomicReference<>();
        when(safetyBriefingRepository.findByBriefingDateAndContextFingerprint(any(), any()))
                .thenAnswer(invocation -> {
                    cacheLookups.countDown();
                    return Optional.ofNullable(cached.get());
                });
        when(queryGenerator.generate(any(), any())).thenAnswer(invocation -> {
            generationStarted.countDown();
            assertThat(releaseGeneration.await(5, TimeUnit.SECONDS)).isTrue();
            return "검색 쿼리";
        });
        when(generator.generateSafetyBriefing(any(), any(), any())).thenReturn("공유 브리핑");
        when(safetyBriefingRepository.save(any(SafetyBriefing.class))).thenAnswer(invocation -> {
            SafetyBriefing briefing = invocation.getArgument(0);
            cached.set(briefing);
            return briefing;
        });

        var executor = Executors.newFixedThreadPool(2);
        try {
            var first = executor.submit(() -> safetyBriefingService.getTodayBriefing());
            assertThat(generationStarted.await(5, TimeUnit.SECONDS)).isTrue();
            var second = executor.submit(() -> safetyBriefingService.getTodayBriefing());
            assertThat(cacheLookups.await(5, TimeUnit.SECONDS)).isTrue();
            releaseGeneration.countDown();

            assertThat(first.get(5, TimeUnit.SECONDS).content()).isEqualTo("공유 브리핑");
            assertThat(second.get(5, TimeUnit.SECONDS)).isEqualTo(first.get());
            verify(queryGenerator).generate(any(), any());
            verify(generator).generateSafetyBriefing(any(), any(), any());
            verify(safetyBriefingRepository).save(any(SafetyBriefing.class));
        } finally {
            releaseGeneration.countDown();
            executor.shutdownNow();
        }
    }

    @Test
    void failedGenerationCanBeRetried() {
        when(queryGenerator.generate(any(), any()))
                .thenThrow(new IllegalStateException("일시적 오류"))
                .thenReturn("검색 쿼리");
        when(generator.generateSafetyBriefing(any(), any(), any())).thenReturn("재시도 브리핑");
        when(safetyBriefingRepository.save(any(SafetyBriefing.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        assertThatThrownBy(() -> safetyBriefingService.getTodayBriefing())
                .isInstanceOf(SafetyBriefingGenerationException.class);
        assertThat(safetyBriefingService.getTodayBriefing().content()).isEqualTo("재시도 브리핑");
        verify(queryGenerator, times(2)).generate(any(), any());
        verify(safetyBriefingRepository).save(any(SafetyBriefing.class));
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
