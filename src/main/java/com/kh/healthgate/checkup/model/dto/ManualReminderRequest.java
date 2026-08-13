package com.kh.healthgate.checkup.model.dto;

import com.kh.healthgate.checkup.model.vo.NotificationChannel;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 수동 알림 발송 요청 DTO
 */
@NoArgsConstructor
@Getter
@Setter
public class ManualReminderRequest {

    /**
     * 알림을 발송할 건강검진 기록 식별자
     */
    private Long checkupId;

    /**
     * 알림 발송 수단
     * SMS 또는 EMAIL
     */
    private NotificationChannel channel;

    /**
     * 발송할 알림 메시지 내용
     */
    private String content;
}