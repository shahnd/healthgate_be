package com.kh.healthgate.opendata.weather.util;

import java.time.LocalDate;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.kh.healthgate.opendata.weather.client.dto.VilageFcstBaseTime;

public class VilageFcstDateTimeUtils {
    private static final Duration FORECAST_AVAILABILITY_DELAY = Duration.ofMinutes(10);

    /** 한국 시각 기준으로 오늘 T3를 우선하고, 제공 전에는 최신 조회 가능 발표 일시를 반환한다. */
    public static LocalDateTime preferredBaseDateTime(LocalDateTime now) {
        LocalDateTime availableThrough = now.minus(FORECAST_AVAILABILITY_DELAY);
        LocalDateTime todayT3 = now.toLocalDate().atTime(VilageFcstBaseTime.T3.toLocalTime());
        return availableThrough.isBefore(todayT3)
                ? latestBaseDateTimeBefore(availableThrough)
                : todayT3;
    }

    public static LocalDateTime latestBaseDateTimeBefore(LocalDateTime dateTime) {
        LocalDate date = dateTime.toLocalDate();
        VilageFcstBaseTime time = VilageFcstBaseTime.latestBefore(dateTime.toLocalTime()).orElse(null);

        if (time == null) {
            date = date.minusDays(1);
            time = VilageFcstBaseTime.last();
        }

        return LocalDateTime.of(date, time.toLocalTime());
    }

    public static LocalDateTime latestForecastDateTimeBefore(LocalDateTime dateTime) {
        return LocalDateTime.of(dateTime.toLocalDate(), LocalTime.of(dateTime.getHour(), 0));
    }
}
