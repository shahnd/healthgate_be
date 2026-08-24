package com.kh.healthgate.checkup.model.vo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 자동 알림 설정을 저장하는 Entity
 * DB의 checkup_reminder_settings 테이블과 연결된다.
 */
@Entity
@Table(name = "checkup_reminder_settings")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
public class CheckupReminderSetting {

    /**
     * 건강검진 알림 설정 식별자(PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkup_reminder_setting_id", nullable = false)
    private Long checkupReminderSettingId;

    /**
     * 알림 설정 종류
     * 예: 검진일 이전 알림, 미검진자 알림
     */
    @Column(
    	name = "checkup_reminder_setting_type",
        nullable = false,
        length = 30
    )
    private String checkupReminderSettingType;

    /**
     * 자동 알림에 사용할 메시지 템플릿
     */
    @Column(
        name = "checkup_reminder_setting_message_template",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String checkupReminderSettingMessageTemplate;

    /**
     * 자동 알림 실행 주기를 나타내는 Cron 표현식
     */
    @Column(
        name = "checkup_reminder_setting_cron_schedule",
        nullable = false,
        length = 30
    )
    private String checkupReminderSettingCronSchedule;

    /**
     * 자동 알림 설정 활성화 여부
     * true: 활성화
     * false: 비활성화
     */
    @Column(
        name = "checkup_reminder_setting_is_active",
        nullable = false
    )
    private boolean checkupReminderSettingIsActive;
}