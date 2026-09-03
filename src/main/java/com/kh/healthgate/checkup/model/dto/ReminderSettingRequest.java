package com.kh.healthgate.checkup.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 자동 알림 설정 등록·수정 요청 DTO
 */
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "건강검진 자동 알림 설정 수정 요청")
public class ReminderSettingRequest {

    /**
     * 알림 설정 종류
     * 예: BEFORE_CHECKUP, INCOMPLETE
     */
    @Schema(description = "알림 설정 종류", example = "BEFORE_CHECKUP", allowableValues = {"BEFORE_CHECKUP", "MISSING_CHECKUP", "AFTER_CHECKUP"})
    private String settingType;

    /**
     * 자동 발송할 메시지 템플릿
     */
    @Schema(description = "자동 발송 메시지 템플릿", example = "건강검진 일정을 확인해 주세요.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String messageTemplate;

    /**
     * 자동 알림 실행 주기인 Cron 표현식
     * 예: 매일 오전 9시 = 0 0 9 * * *
     */
    @Schema(description = "자동 알림 실행 주기인 Spring Cron 표현식", example = "0 0 9 1 * *", requiredMode = Schema.RequiredMode.REQUIRED)
    private String cronSchedule;

    /**
     * 자동 알림 활성화 여부
     */
    @Schema(description = "자동 알림 활성화 여부", example = "true")
    private boolean active;
}
