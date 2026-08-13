package com.kh.healthgate.checkup.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 건강검진 알림 발송 이력을 저장하는 Entity
 * DB의 checkup_reminders 테이블과 연결된다.
 */
@Entity
@Table(name = "checkup_reminders")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
public class CheckupReminder {

    /**
     * 건강검진 알림 발송 이력 식별자(PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkup_reminder_id", nullable = false)
    private Long checkupReminderId;

    /**
     * 알림 발송 채널
     * SMS 또는 EMAIL 값을 문자열로 저장한다.
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "checkup_reminder_channel", nullable = false)
    private NotificationChannel checkupReminderChannel;

    /**
     * 발송한 알림 메시지 내용
     */
    @Column(
        name = "checkup_reminder_content",
        nullable = false,
        columnDefinition = "TEXT"
    )
    private String checkupReminderContent;

    /**
     * 알림을 발송한 날짜와 시간
     * DB의 CURRENT_TIMESTAMP 기본값을 사용한다.
     */
    @Column(
        name = "checkup_reminder_sent_at",
        nullable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime checkupReminderSentAt;

    /**
     * 알림 발송 상태
     * 예: SUCCESS, FAILED
     */
    @Column(
        name = "checkup_reminder_status",
        nullable = false,
        length = 20
    )
    private String checkupReminderStatus;

    /**
     * 수동 발송 여부
     * true: 관리자가 수동으로 발송
     * false: 시스템에서 자동으로 발송
     */
    @Column(name = "checkup_reminder_is_manual", nullable = false)
    private boolean checkupReminderIsManual;

    /**
     * 알림 발송의 대상이 된 건강검진 기록
     *
     * 하나의 건강검진 기록에 여러 알림 이력이
     * 존재할 수 있으므로 다대일(N:1) 관계로 설정한다.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "checkup_id", nullable = false)
    private Checkup checkup;
}