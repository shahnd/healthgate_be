package com.kh.healthgate.weather.external;

/**
 * <p>
 * - ServiceKey: 서비스키
 * - pageNo: 페이지 번호
 * - numOfRows: 한 페이지 결과 수
 * - dataType: 응답자료형식
 * - base_date: 발표일자
 * - base_time: 발표시각
 * - nx: 예보지점 X 좌표
 * - ny: 예보지점 Y 좌표
 * </p>
 */
public record VilageFcstRequest(
        String serviceKey,
        int pageNo,
        int numOfRows,
        // String dataType;
        String baseDate,
        String baseTime,
        int nx,
        int ny) {

    public String dataType() {
        return "JSON";
    }
}