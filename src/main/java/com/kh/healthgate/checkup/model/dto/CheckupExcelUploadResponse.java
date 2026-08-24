package com.kh.healthgate.checkup.model.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 건강검진 결과 Excel 업로드 처리 결과 DTO
 */
@Getter
@AllArgsConstructor
public class CheckupExcelUploadResponse {

    /**
     * Excel에서 읽은 전체 데이터 행 수
     */
    private int totalCount;

    /**
     * 정상적으로 등록 또는 수정된 행 수
     */
    private int successCount;

    /**
     * 처리하지 못한 행 수
     */
    private int failureCount;

    /**
     * 처리하지 못한 행의 오류 내용
     *
     * 예:
     * "3행: 존재하지 않는 사번입니다. (TEST999)"
     */
    private List<String> errors;

    /**
     * 전체 처리 결과 메시지
     */
    private String message;
}