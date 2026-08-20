package com.kh.healthgate.opendata.weather.model.vo;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum VilageFcstCategory {
    // @formatter:off
    PRECIPITATION_PROBABILITY("POP", "강수확률"),
    PRECIPITATION_TYPE(       "PTY", "강수형태"),
    PRECIPITATION_AMOUNT(     "PCP", "1시간 강수량"),
    HUMIDITY(                 "REH", "습도"),
    SNOWFALL_AMOUNT(          "SNO", "1시간 신적설"),
    SKY_CONDITION(            "SKY", "하늘상태"),
    TEMPERATURE(              "TMP", "1시간 기온"),
    MIN_TEMPERATURE(          "TMN", "일 최저기온"),
    MAX_TEMPERATURE(          "TMX", "일 최고기온"),
    EAST_WEST_WIND_SPEED(     "UUU", "풍속(동서성분)"),
    NORTH_SOUTH_WIND_SPEED(   "VVV", "풍속(남북성분)"),
    WAVE_HEIGHT(              "WAV", "파고"),
    WIND_DIRECTION(           "VEC", "풍향"),
    WIND_SPEED(               "WSD", "풍속");
    // @formatter:on

    @Getter
    private final String code;

    @Getter
    private final String displayName;

    @JsonCreator
    public static VilageFcstCategory from(String value) {
        return Arrays.stream(values())
                .filter(category -> category.code.equals(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("알 수 없는 카테고리 코드입니다: " + value));
    }
}
