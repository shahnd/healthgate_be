package com.kh.healthgate.checkup.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 자동 알림 설정 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Schema(description = "건강검진 자동 알림 설정 응답")
public class ReminderSettingResponse {

    /**
     * 자동 알림 설정 식별자
     */
    @Schema(description = "자동 알림 설정 ID", example = "1")
    private Long settingId;

    /**
     * 알림 설정 종류
     */
    @Schema(description = "알림 설정 종류", example = "BEFORE_CHECKUP", allowableValues = {"BEFORE_CHECKUP", "MISSING_CHECKUP", "AFTER_CHECKUP"})
    private String settingType;

    /**
     * 메시지 템플릿
     */
    @Schema(description = "자동 발송 메시지 템플릿", example = "건강검진 일정을 확인해 주세요.")
    private String messageTemplate;

    /**
     * Cron 표현식
     */
    @Schema(description = "자동 알림 실행 주기인 Spring Cron 표현식", example = "0 0 9 1 * *")
    private String cronSchedule;

    /**
     * 활성화 여부
     */
    @Schema(description = "자동 알림 활성화 여부", example = "true")
    private boolean active;
}
