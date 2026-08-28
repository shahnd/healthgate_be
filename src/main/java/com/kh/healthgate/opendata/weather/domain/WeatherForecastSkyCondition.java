package com.kh.healthgate.opendata.weather.domain;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeatherForecastSkyCondition {
    // @formatter:off
    CLEAR(        "1", "맑음"),
    PARTLY_CLOUDY("3", "구름많음"),
    CLOUDY(       "4", "흐림");
    // @formatter:on

    @Getter
    private final String code;
    @Getter
    private final String displayName;

    @JsonCreator
    public static WeatherForecastSkyCondition from(String value) {
        return Arrays.stream(values())
                .filter(sky -> sky.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 날씨 코드입니다: " + value));
    }
}
