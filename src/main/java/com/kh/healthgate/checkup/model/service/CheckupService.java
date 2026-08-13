package com.kh.healthgate.checkup.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.checkup.model.dao.CheckupDao;
import com.kh.healthgate.checkup.model.dto.CheckupStatisticsResponse;

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.checkup.model.dto.CheckupTargetResponse;
import com.kh.healthgate.checkup.model.vo.Checkup;

import java.time.LocalDateTime;

import com.kh.healthgate.checkup.model.dao.CheckupReminderDao;
import com.kh.healthgate.checkup.model.dto.ManualReminderRequest;
import com.kh.healthgate.checkup.model.dto.ReminderResponse;
import com.kh.healthgate.checkup.model.vo.CheckupReminder;

import com.kh.healthgate.checkup.model.dao.CheckupReminderSettingDao;
import com.kh.healthgate.checkup.model.dto.ReminderSettingRequest;
import com.kh.healthgate.checkup.model.dto.ReminderSettingResponse;
import com.kh.healthgate.checkup.model.vo.CheckupReminderSetting;
/**
 * 건강검진 관련 비즈니스 로직을 담당하는 Service
 */
@Service
public class CheckupService {

    @Autowired
    private CheckupDao checkupDao;
    
    @Autowired
    private CheckupReminderDao checkupReminderDao;
    
    @Autowired
    private CheckupReminderSettingDao checkupReminderSettingDao;

    /**
     * 지정한 연도의 건강검진 완료율 통계를 조회한다.
     *
     * @param checkupYear 조회할 검진 대상 연도
     * @return 전체·완료·미완료 인원 및 완료율
     */
    public CheckupStatisticsResponse getCheckupStatistics(Short checkupYear) {

        // 해당 연도의 전체 검진 대상자 수
        long totalCount =
                checkupDao.countByCheckupYear(checkupYear);

        // 해당 연도의 검진 완료자 수
        long completedCount =
                checkupDao.countByCheckupYearAndCheckupDateIsNotNull(
                        checkupYear
                );

        // 검진 미완료자 수
        long incompleteCount = totalCount - completedCount;

        // 전체 대상자가 없으면 0으로 나누지 않도록 완료율을 0으로 처리
        double completionRate = totalCount == 0
                ? 0.0
                : (double) completedCount / totalCount * 100;

        // 소수점 첫째 자리까지만 표시
        completionRate = Math.round(completionRate * 10.0) / 10.0;

        return new CheckupStatisticsResponse(
                checkupYear,
                totalCount,
                completedCount,
                incompleteCount,
                completionRate
        );
    }
    
    /**
     * 지정한 연도의 건강검진 대상자 목록을 조회한다.
     *
     * @param checkupYear 조회할 검진 대상 연도
     * @return 건강검진 대상자 목록
     */
    @Transactional(readOnly = true)
    public List<CheckupTargetResponse> getCheckupTargets(Short checkupYear) {

        // 해당 연도의 건강검진 기록 조회
        List<Checkup> checkupList =
                checkupDao.findByCheckupYearOrderByCheckupIdAsc(checkupYear);

        // Entity 목록을 프론트엔드 응답용 DTO 목록으로 변환
        return checkupList.stream()
                .map(checkup -> new CheckupTargetResponse(
                        checkup.getCheckupId(),
                        checkup.getCheckupYear(),
                        checkup.getCheckupDate(),
                        checkup.getCheckupSummary(),

                        // 검진일이 존재하면 검진 완료
                        checkup.getCheckupDate() != null,

                        checkup.getEmployee().getEmployeeId(),
                        checkup.getEmployee().getEmployeeNo(),
                        checkup.getEmployee().getEmployeeName()
                ))
                .toList();
    }
    
    /**
     * 건강검진 수동 알림 발송 이력을 저장한다.
     *
     * 현재는 실제 SMS·이메일 외부 서비스 연동 전이므로
     * 알림 정보를 발송 성공 상태로 DB에 저장한다.
     *
     * @param request 수동 알림 발송 요청 정보
     * @return 저장된 알림 발송 결과
     */
    @Transactional
    public ReminderResponse sendManualReminder(ManualReminderRequest request) {

        // 요청한 건강검진 기록 조회
        Checkup checkup = checkupDao.findById(request.getCheckupId())
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "건강검진 기록을 찾을 수 없습니다."
                        )
                );

        // 알림 발송 이력 생성
        CheckupReminder reminder = new CheckupReminder();

        reminder.setCheckupReminderChannel(request.getChannel());
        reminder.setCheckupReminderContent(request.getContent());
        reminder.setCheckupReminderSentAt(LocalDateTime.now());
        reminder.setCheckupReminderStatus("SUCCESS");
        reminder.setCheckupReminderIsManual(true);
        reminder.setCheckup(checkup);

        // 알림 발송 이력 저장
        CheckupReminder savedReminder =
                checkupReminderDao.save(reminder);

        return new ReminderResponse(
                savedReminder.getCheckupReminderId(),
                savedReminder.getCheckup().getCheckupId(),
                savedReminder.getCheckupReminderChannel(),
                savedReminder.getCheckupReminderContent(),
                savedReminder.getCheckupReminderSentAt(),
                savedReminder.getCheckupReminderStatus(),
                savedReminder.isCheckupReminderIsManual()
        );
    }
    
    /**
     * 자동 알림 설정을 등록한다.
     */
    @Transactional
    public ReminderSettingResponse createReminderSetting(
            ReminderSettingRequest request) {

        CheckupReminderSetting setting =
                new CheckupReminderSetting();

        setting.setCheckupReminderSettingType(
                request.getSettingType()
        );
        setting.setCheckupReminderSettingMessageTemplate(
                request.getMessageTemplate()
        );
        setting.setCheckupReminderSettingCronSchedule(
                request.getCronSchedule()
        );
        setting.setCheckupReminderSettingIsActive(
                request.isActive()
        );

        CheckupReminderSetting savedSetting =
                checkupReminderSettingDao.save(setting);

        return convertReminderSettingResponse(savedSetting);
    }

    /**
     * 자동 알림 설정 전체 목록을 조회한다.
     */
    @Transactional(readOnly = true)
    public List<ReminderSettingResponse> getReminderSettings() {

        return checkupReminderSettingDao.findAll()
                .stream()
                .map(this::convertReminderSettingResponse)
                .toList();
    }

    /**
     * 자동 알림 설정을 수정한다.
     */
    @Transactional
    public ReminderSettingResponse updateReminderSetting(
            Long settingId,
            ReminderSettingRequest request) {

        CheckupReminderSetting setting =
                checkupReminderSettingDao.findById(settingId)
                        .orElseThrow(() ->
                                new IllegalArgumentException(
                                        "자동 알림 설정을 찾을 수 없습니다."
                                )
                        );

        setting.setCheckupReminderSettingType(
                request.getSettingType()
        );
        setting.setCheckupReminderSettingMessageTemplate(
                request.getMessageTemplate()
        );
        setting.setCheckupReminderSettingCronSchedule(
                request.getCronSchedule()
        );
        setting.setCheckupReminderSettingIsActive(
                request.isActive()
        );

        CheckupReminderSetting savedSetting =
                checkupReminderSettingDao.save(setting);

        return convertReminderSettingResponse(savedSetting);
    }

    /**
     * 자동 알림 설정 Entity를 응답 DTO로 변환한다.
     */
    private ReminderSettingResponse convertReminderSettingResponse(
            CheckupReminderSetting setting) {

        return new ReminderSettingResponse(
                setting.getCheckupReminderSettingId(),
                setting.getCheckupReminderSettingType(),
                setting.getCheckupReminderSettingMessageTemplate(),
                setting.getCheckupReminderSettingCronSchedule(),
                setting.isCheckupReminderSettingIsActive()
        );
    }
    
    /**
     * 건강검진 알림 발송 이력 전체를 최신순으로 조회한다.
     */
    @Transactional(readOnly = true)
    public List<ReminderResponse> getReminderHistory() {

        return checkupReminderDao
                .findAllByOrderByCheckupReminderSentAtDesc()
                .stream()
                .map(reminder -> new ReminderResponse(
                        reminder.getCheckupReminderId(),
                        reminder.getCheckup().getCheckupId(),
                        reminder.getCheckupReminderChannel(),
                        reminder.getCheckupReminderContent(),
                        reminder.getCheckupReminderSentAt(),
                        reminder.getCheckupReminderStatus(),
                        reminder.isCheckupReminderIsManual()
                ))
                .toList();
    }
}