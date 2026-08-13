package com.kh.healthgate.checkup.model.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 자동 알림 설정 등록·수정 요청 DTO
 */
@NoArgsConstructor
@Getter
@Setter
public class ReminderSettingRequest {

    /**
     * 알림 설정 종류
     * 예: BEFORE_CHECKUP, INCOMPLETE
     */
    private String settingType;

    /**
     * 자동 발송할 메시지 템플릿
     */
    private String messageTemplate;

    /**
     * 자동 알림 실행 주기인 Cron 표현식
     * 예: 매일 오전 9시 = 0 0 9 * * *
     */
    private String cronSchedule;

    /**
     * 자동 알림 활성화 여부
     */
    private boolean active;
}