package com.kh.healthgate.opendata.weather.model.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.kh.healthgate.opendata.weather.WeatherApiClient;
import com.kh.healthgate.opendata.weather.model.dao.WeatherForecastRepository;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstResponse;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastPrecipitationType;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastSkyCondition;
import com.kh.healthgate.opendata.weather.utils.VilageFcstDateTimeFormatter;

import tools.jackson.databind.ObjectMapper;

@ExtendWith(MockitoExtension.class)
public class WeatherServiceTest {

    @Mock
    private WeatherForecastRepository weatherForecastRepository;

    @Mock
    private WeatherApiClient client;

    @InjectMocks
    private WeatherService weatherService;

    @Captor
    private ArgumentCaptor<List<WeatherForecast>> forecastListCaptor;

    private VilageFcstResponse loadFixture(String filename) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();

        return objectMapper.readValue(
                getClass()
                        .getResourceAsStream("/fixture/weather/" + filename),
                VilageFcstResponse.class);
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
                new BigDecimal("0.0"),
                new BigDecimal("0.1"),
                WeatherForecastPrecipitationType.RAIN,
                WeatherForecastSkyCondition.CLOUDY,
                WeatherForecastLocation.YEOKSAM1);

        // DB에 없음
        when(weatherForecastRepository.existsByForecastAt(forecastAt)).thenReturn(false);

        // 서비스가 인덱싱한 이후에는 예상되는 값 반환
        when(weatherForecastRepository.findByForecastAt(forecastAt)).thenReturn(Optional.of(expected));

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
