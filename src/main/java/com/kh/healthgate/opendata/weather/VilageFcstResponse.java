package com.kh.healthgate.opendata.weather;

import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

public record VilageFcstResponse(
        Response response) {
    public record Response(
            Header header,
            Body body) {
    }

    public record Header(
            ResultCode resultCode,
            String resultMsg) {
    }

    @RequiredArgsConstructor
    public enum ResultCode {
        // @formatter:off
        NORMAL_SERVICE(                                  "00", "정상"),
        APPLICATION_ERROR(                               "01", "애플리케이션 에러"),
        DB_ERROR(                                        "02", "데이터베이스 에러"),
        NODATA_ERROR(                                    "03", "데이터 없음"),
        HTTP_ERROR(                                      "04", "HTTP 에러"),
        SERVICETIME_OUT(                                 "05", "서비스 연결 실패"),
        INVALID_REQUEST_PARAMETER_ERROR(                 "10", "잘못된 요청 파라미터"),
        NO_MANDATORY_REQUEST_PARAMETERS_ERROR(           "11", "필수 요청 파라미터 없음"),
        NO_OPENAPI_SERVICE_ERROR(                        "12", "해당 OpenAPI 서비스가 없거나 폐기됨"),
        SERVICE_ACCESS_DENIED_ERROR(                     "20", "서비스 접근 거부"),
        TEMPORARILY_DISABLE_THE_SERVICEKEY_ERROR(        "21", "일시적으로 사용할 수 없는 서비스 키"),
        LIMITED_NUMBER_OF_SERVICE_REQUESTS_EXCEEDS_ERROR("22", "서비스 요청 제한 횟수 초과"),
        SERVICE_KEY_IS_NOT_REGISTERED_ERROR(             "30", "등록되지 않은 서비스 키"),
        DEADLINE_HAS_EXPIRED_ERROR(                      "31", "기한 만료된 서비스 키"),
        UNREGISTERED_IP_ERROR(                           "32", "등록되지 않은 IP"),
        UNSIGNED_CALL_ERROR(                             "33", "서명되지 않은 호출"),
        UNKNOWN_ERROR(                                   "99", "기타 에러");
        // @formatter:on

        @Getter
        private final String code;
        @Getter
        private final String displayName;

        @JsonCreator
        public static ResultCode fromCode(String value) {
            return Arrays.stream(values())
                    .filter(resultCode -> resultCode.code.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("알 수 없는 상태 코드입니다: " + value));
        }
    }

    public record Body(
            String dataType,
            Items items) {
    }

    public record Items(
            List<Item> item) {
    }

    public record Item(
            String baseDate,
            String baseTime,
            Category category,
            String fcstDate,
            String fcstTime,
            String fcstValue,
            int nx,
            int ny) {
    }

    @RequiredArgsConstructor
    public enum Category {
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
        public static Category fromCode(String value) {
            return Arrays.stream(values())
                    .filter(category -> category.code.equals(value))
                    .findFirst()
                    .orElseThrow(() -> new IllegalArgumentException("알 수 없는 카테고리 코드입니다: " + value));
        }
    }
}