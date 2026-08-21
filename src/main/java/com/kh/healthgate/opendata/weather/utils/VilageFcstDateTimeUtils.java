package com.kh.healthgate.opendata.weather.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.kh.healthgate.opendata.weather.model.vo.VilageFcstBaseTime;

public class VilageFcstDateTimeUtils {
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
