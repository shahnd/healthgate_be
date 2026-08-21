package com.kh.healthgate.checkup.model.dto;

import java.time.LocalDate;

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
public class CheckupTargetResponse {

    /**
     * 건강검진 기록 식별자
     */
    private Long checkupId;

    /**
     * 건강검진 대상 연도
     */
    private Short checkupYear;

    /**
     * 검진일
     * 미완료 상태이면 null
     */
    private LocalDate checkupDate;

    /**
     * 검진 내용 및 결과 요약
     */
    private String checkupSummary;

    /**
     * 검진 완료 여부
     */
    private boolean completed;

    /**
     * 직원 식별자
     */
    private Long employeeId;

    /**
     * 사번
     */
    private String employeeNo;

    /**
     * 직원 이름
     */
    private String employeeName;
}