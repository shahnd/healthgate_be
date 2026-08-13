package com.kh.healthgate.checkup.model.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.checkup.model.vo.NotificationChannel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 알림 발송 결과 응답 DTO
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReminderResponse {

    private Long reminderId;

    private Long checkupId;

    private NotificationChannel channel;

    private String content;

    private LocalDateTime sentAt;

    private String status;

    private boolean manual;
}