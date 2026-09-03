package com.kh.healthgate.checkup.model.dto;

import com.kh.healthgate.checkup.model.vo.NotificationChannel;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 수동 알림 발송 요청 DTO
 */
@NoArgsConstructor
@Getter
@Setter
@Schema(description = "건강검진 수동 알림 발송 요청")
public class ManualReminderRequest {

    /**
     * 알림을 발송할 건강검진 기록 식별자
     */
    @Schema(description = "알림을 발송할 건강검진 기록 ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    private Long checkupId;

    /**
     * 알림 발송 수단
     * SMS 또는 EMAIL
     */
    @Schema(description = "알림 발송 채널", example = "EMAIL", allowableValues = {"EMAIL", "SMS"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private NotificationChannel channel;

    /**
     * 발송할 알림 메시지 내용
     */
    @Schema(description = "발송할 알림 메시지", example = "건강검진을 완료해 주세요.", requiredMode = Schema.RequiredMode.REQUIRED)
    private String content;
}
