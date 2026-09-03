package com.kh.healthgate.checkup.model.dto;

import java.time.LocalDate;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 대상자 목록 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "건강검진 대상자 조회 응답")
public class CheckupTargetResponse {

    /**
     * 건강검진 기록 식별자
     */
    @Schema(description = "건강검진 기록 ID", example = "1")
    private Long checkupId;

    /**
     * 건강검진 대상 연도
     */
    @Schema(description = "건강검진 대상 연도", example = "2026")
    private Short checkupYear;

    /**
     * 검진일
     * 미완료 상태이면 null
     */
    @Schema(description = "검진일이며 미완료인 경우 null", example = "2026-06-15", nullable = true)
    private LocalDate checkupDate;

    /**
     * 검진 내용 및 결과 요약
     */
    @Schema(description = "건강검진 결과 요약", example = "혈압 정상, 특이 소견 없음", nullable = true)
    private String checkupSummary;

    /**
     * 검진 완료 여부
     */
    @Schema(description = "검진 완료 여부", example = "true")
    private boolean completed;

    /**
     * 직원 식별자
     */
    @Schema(description = "직원 ID", example = "1")
    private Long employeeId;

    /**
     * 사번
     */
    @Schema(description = "사번", example = "emp01")
    private String employeeNo;

    /**
     * 직원 이름
     */
    @Schema(description = "직원 이름", example = "최민준")
    private String employeeName;
}
