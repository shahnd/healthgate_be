package com.kh.healthgate.checkup.scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.checkup.model.dao.CheckupDao;
import com.kh.healthgate.checkup.model.dao.CheckupReminderDao;
import com.kh.healthgate.checkup.model.dao.CheckupReminderSettingDao;
import com.kh.healthgate.checkup.model.vo.Checkup;
import com.kh.healthgate.checkup.model.vo.CheckupReminder;
import com.kh.healthgate.checkup.model.vo.CheckupReminderSetting;
import com.kh.healthgate.checkup.model.vo.NotificationChannel;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 건강검진 자동 알림 실행 스케줄러
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CheckupReminderScheduler {

    private final CheckupDao checkupDao;

    private final CheckupReminderDao checkupReminderDao;

    private final CheckupReminderSettingDao
            checkupReminderSettingDao;

    /**
     * 매분 0초마다 활성화된 자동 알림 설정을 확인한다.
     *
     * 설정의 Cron 실행 시간이 현재 시각과 일치하면
     * 현재 연도의 미수검자에게 자동 알림을 발송한 것으로
     * 처리하고 발송 이력을 DB에 저장한다.
     */
    @Scheduled(cron = "0 * * * * *")
    @Transactional
    public void executeAutomaticReminder() {

        LocalDateTime currentMinute = LocalDateTime.now()
                .truncatedTo(ChronoUnit.MINUTES);

        log.info(
                "건강검진 자동 알림 설정 확인: {}",
                currentMinute
        );

        List<CheckupReminderSetting> activeSettings =
                checkupReminderSettingDao
                        .findByCheckupReminderSettingIsActiveTrue();

        for (CheckupReminderSetting setting : activeSettings) {

            String cronSchedule =
                    setting.getCheckupReminderSettingCronSchedule();

            if (!isExecutionTime(cronSchedule, currentMinute)) {
                continue;
            }

            sendAutomaticReminders(
                    setting,
                    currentMinute
            );
        }
    }

    /**
     * 저장된 Cron 표현식이 현재 분과 일치하는지 확인한다.
     */
    private boolean isExecutionTime(
            String cronSchedule,
            LocalDateTime currentMinute) {

        try {
            CronExpression cronExpression =
                    CronExpression.parse(cronSchedule);

            LocalDateTime nextExecution =
                    cronExpression.next(
                            currentMinute.minusSeconds(1)
                    );

            return currentMinute.equals(nextExecution);

        } catch (IllegalArgumentException exception) {

            log.error(
                    "올바르지 않은 Cron 표현식입니다: {}",
                    cronSchedule,
                    exception
            );

            return false;
        }
    }

    /**
     * 현재 연도의 미수검자에게 자동 알림 이력을 저장한다.
     */
    private void sendAutomaticReminders(
            CheckupReminderSetting setting,
            LocalDateTime sentAt) {

        short currentYear =
                (short) sentAt.getYear();

        List<Checkup> incompleteCheckups =
                checkupDao
                        .findByCheckupYearAndCheckupDateIsNull(
                                currentYear
                        );

        log.info(
                "{}년 건강검진 자동 알림 실행: 미수검자 {}명",
                currentYear,
                incompleteCheckups.size()
        );

        for (Checkup checkup : incompleteCheckups) {

            LocalDateTime startTime =
                    sentAt.truncatedTo(ChronoUnit.MINUTES);

            LocalDateTime endTime =
                    startTime.plusMinutes(1);

            boolean alreadySent =
                    checkupReminderDao
                            .existsByCheckup_CheckupIdAndCheckupReminderIsManualFalseAndCheckupReminderSentAtBetween(
                                    checkup.getCheckupId(),
                                    startTime,
                                    endTime
                            );

            // 같은 실행 시각에 이미 자동 발송 이력이 있으면 저장하지 않는다.
            if (alreadySent) {

                log.info(
                        "자동 알림 중복 저장 생략: checkupId={}",
                        checkup.getCheckupId()
                );

                continue;
            }

            CheckupReminder reminder =
                    new CheckupReminder();

            reminder.setCheckupReminderChannel(
                    NotificationChannel.SMS
            );

            reminder.setCheckupReminderContent(
                    setting.getCheckupReminderSettingMessageTemplate()
            );

            reminder.setCheckupReminderSentAt(sentAt);
            reminder.setCheckupReminderStatus("SUCCESS");
            reminder.setCheckupReminderIsManual(false);
            reminder.setCheckup(checkup);

            checkupReminderDao.save(reminder);
        }
            
    }
}