package com.kh.healthgate.opendata.weather.model.vo;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum WeatherForecastPrecipitationType {
    // @formatter:off
    NONE(     "0", "없음"),
    RAIN(     "1", "비"),
    RAIN_SNOW("2", "비/눈"),
    SNOW(     "3", "눈"),
    SHOWER(   "4", "소나기");
    // @formatter:on

    @Getter
    private final String code;
    @Getter
    private final String displayName;

    @JsonCreator
    public static WeatherForecastPrecipitationType from(String value) {
        return Arrays.stream(values())
                .filter(precipitation -> precipitation.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 강수향 코드입니다: " + value));
    }
}
