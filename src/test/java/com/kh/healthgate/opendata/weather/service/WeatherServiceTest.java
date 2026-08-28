package com.kh.healthgate.opendata.weather.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kh.healthgate.opendata.weather.client.WeatherApiClient;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstRequest;
import com.kh.healthgate.opendata.weather.client.dto.VilageFcstResponse;
import com.kh.healthgate.opendata.weather.domain.WeatherForecast;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastPrecipitationType;
import com.kh.healthgate.opendata.weather.domain.WeatherForecastSkyCondition;
import com.kh.healthgate.opendata.weather.repository.WeatherForecastRepository;
import com.kh.healthgate.opendata.weather.util.VilageFcstDateTimeFormatter;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherForecastRepository weatherForecastRepository;

    @Mock
    private WeatherApiClient client;

    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<List<WeatherForecast>> forecastListCaptor;

    @BeforeEach
    void setUp() {
        Clock clock = Clock.fixed(
                Instant.parse("2026-08-21T00:00:00Z"),
                ZoneId.of("Asia/Seoul"));
        weatherService = new WeatherService(weatherForecastRepository, client, clock);
    }

    private VilageFcstResponse loadFixture(String filename) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                getClass()
                        .getResourceAsStream("/fixture/weather/" + filename),
                VilageFcstResponse.class);
    }

    @Test
    void recomputesForecastDatesForEveryIndexingRequest() throws IOException {
        // given
        Clock clock = mock(Clock.class);
        ZoneId seoul = ZoneId.of("Asia/Seoul");
        when(clock.getZone()).thenReturn(seoul);
        when(clock.instant()).thenReturn(
                Instant.parse("2026-08-20T15:00:00Z"),
                Instant.parse("2026-08-21T15:00:00Z"));
        weatherService = new WeatherService(weatherForecastRepository, client, clock);
        when(client.getVilageFcst(any())).thenReturn(loadFixture("vilage-fcst-success.json"));

        // when
        weatherService.indexVilageFcst(WeatherForecastLocation.YEOKSAM1);
        weatherService.indexVilageFcst(WeatherForecastLocation.YEOKSAM1);

        // then
        ArgumentCaptor<VilageFcstRequest> requestCaptor = ArgumentCaptor.forClass(VilageFcstRequest.class);
        verify(client, times(2)).getVilageFcst(requestCaptor.capture());
        assertThat(requestCaptor.getAllValues())
                .extracting(VilageFcstRequest::baseDate)
                .containsExactly("20260821", "20260822");

        verify(weatherForecastRepository, times(2)).saveAll(forecastListCaptor.capture());
        assertThat(forecastListCaptor.getAllValues().get(0).getFirst().getForecastAt())
                .isEqualTo(LocalDateTime.of(2026, 8, 21, 9, 0));
        assertThat(forecastListCaptor.getAllValues().get(1).getFirst().getForecastAt())
                .isEqualTo(LocalDateTime.of(2026, 8, 22, 9, 0));
    }

    @Test
    void updatesExistingForecastsInsteadOfInsertingDuplicates() throws IOException {
        // given
        LocalDateTime forecastAt = LocalDateTime.of(2026, 8, 21, 9, 0);
        WeatherForecast existingForecast = new WeatherForecast(
                forecastAt,
                new BigDecimal("20"),
                new BigDecimal("50"),
                new BigDecimal("0"),
                "강수없음",
                "5.0cm 이상",
                new BigDecimal("1.0"),
                WeatherForecastPrecipitationType.NONE,
                WeatherForecastSkyCondition.CLEAR,
                WeatherForecastLocation.YEOKSAM1);
        when(client.getVilageFcst(any())).thenReturn(loadFixture("vilage-fcst-success.json"));
        when(weatherForecastRepository.findByForecastAtBetweenAndLocation(
                LocalDateTime.of(2026, 8, 21, 8, 0),
                LocalDateTime.of(2026, 8, 24, 0, 0),
                WeatherForecastLocation.YEOKSAM1))
                .thenReturn(List.of(existingForecast));

        // when
        weatherService.indexVilageFcst(WeatherForecastLocation.YEOKSAM1);

        // then
        verify(weatherForecastRepository).saveAll(forecastListCaptor.capture());
        WeatherForecast savedForecast = forecastListCaptor.getValue().getFirst();
        assertThat(savedForecast).isSameAs(existingForecast);
        assertThat(savedForecast.getTemperature()).isEqualByComparingTo("26");
        assertThat(savedForecast.getHumidity()).isEqualByComparingTo("85");
        assertThat(savedForecast.getPrecipitationProbability()).isEqualByComparingTo("60");
        assertThat(savedForecast.getPrecipitation()).isEqualTo("1mm 미만");
        assertThat(savedForecast.getSnowfall()).isEqualTo("적설없음");
        assertThat(savedForecast.getWindSpeed()).isEqualByComparingTo("0.1");
        assertThat(savedForecast.getPrecipitationType()).isEqualTo(WeatherForecastPrecipitationType.RAIN);
        assertThat(savedForecast.getSkyCondition()).isEqualTo(WeatherForecastSkyCondition.CLOUDY);
    }

    @Test
    public void getsForecastAt() throws IOException {
        // given
        LocalDateTime forecastAt = VilageFcstDateTimeFormatter.toLocalDateTime("202608210900");
        WeatherForecastLocation location = WeatherForecastLocation.YEOKSAM1;

        when(client.getVilageFcst(any())).thenReturn(
                loadFixture("vilage-fcst-success.json"));

        WeatherForecast expected = new WeatherForecast(
                forecastAt,
                new BigDecimal("26"),
                new BigDecimal("85"),
                new BigDecimal("60"),
                "1mm 미만",
                "적설없음",
                new BigDecimal("0.1"),
                WeatherForecastPrecipitationType.RAIN,
                WeatherForecastSkyCondition.CLOUDY,
                WeatherForecastLocation.YEOKSAM1);

        // DB에 없음
        when(weatherForecastRepository.existsByForecastAtAndLocation(forecastAt, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(false);

        // 서비스가 인덱싱한 이후에는 예상되는 값 반환
        when(weatherForecastRepository.findByForecastAtAndLocation(forecastAt, WeatherForecastLocation.YEOKSAM1))
                .thenReturn(Optional.of(expected));

        // when
        weatherService.getWeatherForecastAt(forecastAt, location);

        // then
        // DB에 저장된 값 캡처
        verify(weatherForecastRepository)
                .saveAll(forecastListCaptor.capture());

        WeatherForecast actual = forecastListCaptor.getValue().getFirst();

        // DB에 저장된 값은 예상과 같아야 함
        assertThat(actual)
                .usingRecursiveComparison()
                .ignoringFields("createdAt") // createdAt 필드는 제외
                .isEqualTo(expected);
    }
}
