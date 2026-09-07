package com.kh.healthgate.checkup.model.dto;

import java.time.LocalDateTime;

import com.kh.healthgate.checkup.model.vo.NotificationChannel;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "건강검진 알림 발송 이력 응답")
public class ReminderResponse {

    @Schema(description = "알림 발송 이력 ID", example = "1")
    private Long reminderId;

    @Schema(description = "건강검진 기록 ID", example = "1")
    private Long checkupId;

    @Schema(description = "알림 발송 채널", example = "EMAIL", allowableValues = {"EMAIL", "SMS"})
    private NotificationChannel channel;

    @Schema(description = "발송한 알림 내용", example = "건강검진을 완료해 주세요.")
    private String content;

    @Schema(description = "알림 발송 일시", example = "2026-09-03T10:30:00")
    private LocalDateTime sentAt;

    @Schema(description = "알림 발송 상태", example = "SUCCESS")
    private String status;

    @Schema(description = "수동 발송 여부", example = "true")
    private boolean manual;
}
