package com.kh.healthgate.checkup.model.dto;

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
public class ReminderSettingResponse {

    /**
     * 자동 알림 설정 식별자
     */
    private Long settingId;

    /**
     * 알림 설정 종류
     */
    private String settingType;

    /**
     * 메시지 템플릿
     */
    private String messageTemplate;

    /**
     * Cron 표현식
     */
    private String cronSchedule;

    /**
     * 활성화 여부
     */
    private boolean active;
}