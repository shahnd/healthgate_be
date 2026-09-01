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
import com.kh.healthgate.checkup.model.service.EmailService;

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

    private final EmailService emailService;
    
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
     * 현재 연도의 미검진 직원에게 실제 이메일을 발송하고
     * 대상자별 성공·실패 결과를 알림 이력에 저장한다.
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
                "{}년 건강검진 자동 이메일 알림 실행: 미검진자 {}명",
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

            /*
             * 같은 실행 시간에 이미 자동 발송 이력이 있다면
             * 중복 이메일을 보내지 않는다.
             */
            if (alreadySent) {

                log.info(
                        "자동 이메일 중복 발송 생략: checkupId={}",
                        checkup.getCheckupId()
                );

                continue;
            }

            String status;

            try {
                /*
                 * 건강검진 기록과 연결된 직원의 실제 이메일로
                 * 자동 건강검진 안내 메일을 발송한다.
                 */
                emailService.sendCheckupReminder(
                        checkup.getEmployee().getEmail(),
                        checkup.getEmployee().getName(),
                        setting.getCheckupReminderSettingMessageTemplate()
                );

                status = "SUCCESS";

                log.info(
                        "건강검진 자동 이메일 발송 성공: "
                        + "checkupId={}, employeeNumber={}, email={}",
                        checkup.getCheckupId(),
                        checkup.getEmployee().getEmployeeNumber(),
                        checkup.getEmployee().getEmail()
                );

            } catch (RuntimeException exception) {

                status = "FAILED";

                log.error(
                        "건강검진 자동 이메일 발송 실패: "
                        + "checkupId={}, employeeNumber={}, email={}",
                        checkup.getCheckupId(),
                        checkup.getEmployee().getEmployeeNumber(),
                        checkup.getEmployee().getEmail(),
                        exception
                );
            }

            /*
             * 실제 이메일 발송 결과를 알림 이력에 저장한다.
             */
            CheckupReminder reminder =
                    new CheckupReminder();

            reminder.setCheckupReminderChannel(
                    NotificationChannel.EMAIL
            );

            reminder.setCheckupReminderContent(
                    setting.getCheckupReminderSettingMessageTemplate()
            );

            reminder.setCheckupReminderSentAt(sentAt);
            reminder.setCheckupReminderStatus(status);
            reminder.setCheckupReminderIsManual(false);
            reminder.setCheckup(checkup);

            checkupReminderDao.save(reminder);
        }
    }
}