package com.kh.healthgate.opendata.weather.model.vo;

import java.time.LocalTime;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.kh.healthgate.opendata.weather.utils.VilageFcstDateTimeFormatter;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum VilageFcstBaseTime {
    T1("0200"),
    T2("0500"),
    T3("0800"),
    T4("1100"),
    T5("1400"),
    T6("1700"),
    T7("2000"),
    T8("2300");

    @JsonValue
    private final String value;

    @JsonCreator
    public static VilageFcstBaseTime of(String value) {
        return Arrays.stream(values())
                .filter(baseTime -> baseTime.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("유효하지 않은 VilageFcstBaseTime 값입니다: " + value));
    }

    public static VilageFcstBaseTime from(LocalTime time) {
        return of(VilageFcstDateTimeFormatter.toTimeString(time));
    }

    public LocalTime toLocalTime() {
        return VilageFcstDateTimeFormatter.toLocalTime(value);
    }

    public static Optional<VilageFcstBaseTime> latestBefore(LocalTime currentTime) {
        return Arrays.stream(VilageFcstBaseTime.values())
                .filter(candidate -> !candidate.toLocalTime().isAfter(currentTime))
                .max(Comparator.comparing(time -> time.toLocalTime()));
    }

    public static VilageFcstBaseTime last() {
        return T8;
    }

    @Override
    public String toString() {
        return value;
    }
}
