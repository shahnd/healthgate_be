package com.kh.healthgate.opendata.weather.model.service;

import static com.kh.healthgate.opendata.weather.utils.VilageFcstDateTimeUtils.latestForecastDateTimeBefore;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.opendata.weather.WeatherApiClient;
import com.kh.healthgate.opendata.weather.exceptions.WeatherForecastException;
import com.kh.healthgate.opendata.weather.model.dao.WeatherForecastRepository;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstBaseTime;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstCategory;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstRequest;
import com.kh.healthgate.opendata.weather.model.vo.VilageFcstResponse;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecast;
import com.kh.healthgate.opendata.weather.model.vo.WeatherForecastLocation;
import com.kh.healthgate.opendata.weather.utils.VilageFcstDateTimeFormatter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class WeatherService {
    private final WeatherForecastRepository weatherForecastRepository;
    private final WeatherApiClient client;
    private final Clock clock;

    private static final Long DETAILED_DURATION_DAYS = 3L;

    private boolean isValidForecastDateTime(
            LocalDateTime dateTime,
            LocalDateTime baseDateTime,
            LocalDateTime detailedUntil) {
        return dateTime.isAfter(baseDateTime)
                && (dateTime.isBefore(detailedUntil) || dateTime.isEqual(detailedUntil));
    }

    @Transactional
    public void indexVilageFcst(WeatherForecastLocation location) {
        LocalDate baseDate = LocalDate.now(clock);
        LocalTime baseTime = VilageFcstBaseTime.T3.toLocalTime();
        LocalDateTime baseDateTime = LocalDateTime.of(baseDate, baseTime);
        LocalDateTime detailedUntil = baseDate.atStartOfDay().plusDays(DETAILED_DURATION_DAYS);

        VilageFcstResponse response = client.getVilageFcst(new VilageFcstRequest(
                1,
                10000,
                VilageFcstDateTimeFormatter.toDateString(baseDate),
                VilageFcstDateTimeFormatter.toTimeString(baseTime),
                location.getX(),
                location.getY()));

        List<WeatherForecast> forecasts = response.response().body().items().item().stream()
                // 예보일시 기준 그룹화
                .collect(Collectors.groupingBy(item -> VilageFcstDateTimeFormatter
                        .toLocalDateTime(item.fcstDate(), item.fcstTime())))
                .entrySet().stream()
                // 상세 데이터만 필터링
                .filter((entry) -> isValidForecastDateTime(entry.getKey(), baseDateTime, detailedUntil))
                // 그룹을 WeatherForecast로 매핑
                .map(entry -> WeatherForecast.of(
                        entry.getKey(),
                        location,
                        entry.getValue().stream().collect(Collectors.toMap(
                                item -> VilageFcstCategory.from(item.category()),
                                item -> item.fcstValue()))))
                // 예보일시 기준 정렬
                .sorted(Comparator.comparing(forecast -> forecast.getForecastAt()))
                .toList();

        weatherForecastRepository.saveAll(forecasts);

        log.info("forecast indexed");
    }

    @Transactional
    public WeatherForecast getWeatherForecastAt(LocalDateTime forecastAt, WeatherForecastLocation location) {
        if (forecastAt.getMinute() != 0) {
            throw new IllegalArgumentException("forecastAt은 정각이어야 합니다: " + forecastAt);
        }

        if (!weatherForecastRepository.existsByForecastAtAndLocation(forecastAt, location)) {
            log.warn("예보가 DB에 존재하지 않습니다: " + forecastAt + ": " + location);

            indexVilageFcst(location);
        }

        return weatherForecastRepository.findByForecastAtAndLocation(forecastAt, location)
                .orElseThrow(() -> new WeatherForecastException(
                        "해당 예보를 불러올 수 없습니다: " + forecastAt + ": " + location.getDisplayName()));
    }

    public WeatherForecast getLatestForecast(LocalDateTime forecastAt, WeatherForecastLocation location) {
        return getWeatherForecastAt(latestForecastDateTimeBefore(forecastAt), location);
    }

    @Transactional
    public List<WeatherForecast> findTodayBusinessHoursForecasts(WeatherForecastLocation location) {
        return findBusinessHoursForecasts(LocalDate.now(), location);
    }

    @Transactional
    public List<WeatherForecast> findBusinessHoursForecasts(
            LocalDate forecastDate,
            WeatherForecastLocation location) {
        LocalDateTime start = LocalDateTime.of(forecastDate, LocalTime.of(9, 0));
        LocalDateTime end = LocalDateTime.of(forecastDate, LocalTime.of(18, 0));

        if (!weatherForecastRepository.existsByForecastAtBetweenAndLocation(start, end, location)) {
            log.warn("예보가 DB에 존재하지 않습니다: " + start + ": " + end + ": " + location);

            indexVilageFcst(location);
        }

        return weatherForecastRepository.findByForecastAtBetweenAndLocation(start, end, location);
    }
}
