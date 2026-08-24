package com.kh.healthgate.checkup.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 완료율 통계 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CheckupStatisticsResponse {

    /**
     * 조회 대상 연도
     */
    private Short checkupYear;

    /**
     * 전체 검진 대상자 수
     */
    private long totalCount;

    /**
     * 검진 완료자 수
     */
    private long completedCount;

    /**
     * 검진 미완료자 수
     */
    private long incompleteCount;

    /**
     * 검진 완료율
     * 예: 75.0
     */
    private double completionRate;
}