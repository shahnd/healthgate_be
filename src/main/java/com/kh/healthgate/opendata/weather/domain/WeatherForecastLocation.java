package com.kh.healthgate.opendata.weather.domain;

import java.util.Arrays;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum WeatherForecastLocation {
    // 일단 강남구만
    // @formatter:off
    SINSA(    61, 126, "신사동"),
    NONHYEON1(61, 125, "논현1동"),
    NONHYEON2(61, 126, "논현2동"),
    APGUJEONG(61, 126, "압구정동"),
    CHEONGDAM(61, 126, "청담동"),
    SAMSEONG1(61, 125, "삼성1동"),
    SAMSEONG2(61, 125, "삼성2동"),
    DAECHI1(  61, 125, "대치1동"),
    DAECHI2(  61, 125, "대치2동"),
    DAECHI4(  61, 125, "대치4동"),
    YEOKSAM1( 61, 125, "역삼1동"),
    YEOKSAM2( 61, 125, "역삼2동"),
    DOGOK1(   61, 125, "도곡1동"),
    DOGOK2(   61, 125, "도곡2동"),
    GAEPO1(   61, 125, "개포1동"),
    GAEPO2(   62, 125, "개포2동"),
    GAEPO3(   62, 125, "개포3동"),
    GAEPO4(   61, 125, "개포4동"),
    SEGOK(    62, 125, "세곡동"),
    ILWONBON( 62, 125, "일원본동"),
    ILWON1(   62, 125, "일원1동"),
    SUSEO(    62, 125, "수서동");
    // @formatter:on

    private final int x;
    private final int y;
    private final String displayName;

    public static WeatherForecastLocation at(int x, int y) {
        return Arrays.stream(values())
                .filter(location -> location.x == x && location.y == y)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("해당 좌표의 지역을 찾을 수 없습니다: " + x + ", " + y));
    }
}
