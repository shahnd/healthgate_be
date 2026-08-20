package com.kh.healthgate.opendata.weather;

import java.util.Arrays;

import com.fasterxml.jackson.annotation.JsonCreator;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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