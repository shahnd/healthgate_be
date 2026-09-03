package com.kh.healthgate.checkup.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "건강검진 완료율 통계 응답")
public class CheckupStatisticsResponse {

    /**
     * 조회 대상 연도
     */
    @Schema(description = "조회한 검진 연도", example = "2026")
    private Short checkupYear;

    /**
     * 전체 검진 대상자 수
     */
    @Schema(description = "전체 검진 대상자 수", example = "35")
    private long totalCount;

    /**
     * 검진 완료자 수
     */
    @Schema(description = "검진 완료자 수", example = "20")
    private long completedCount;

    /**
     * 검진 미완료자 수
     */
    @Schema(description = "검진 미완료자 수", example = "15")
    private long incompleteCount;

    /**
     * 검진 완료율
     * 예: 75.0
     */
    @Schema(description = "건강검진 완료율(%)", example = "57.1")
    private double completionRate;
}
