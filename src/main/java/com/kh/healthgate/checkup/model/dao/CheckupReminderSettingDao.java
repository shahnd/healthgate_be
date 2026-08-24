package com.kh.healthgate.checkup.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.checkup.model.vo.CheckupReminderSetting;

/**
 * 건강검진 자동 알림 설정의 DB 접근을 담당하는 Repository
 */
public interface CheckupReminderSettingDao
        extends JpaRepository<CheckupReminderSetting, Long> {

    /**
     * 활성화된 자동 알림 설정 목록 조회
     *
     * 자동 알림 스케줄러에서 현재 실행할 설정을
     * 확인하기 위해 사용한다.
     */
    List<CheckupReminderSetting>
            findByCheckupReminderSettingIsActiveTrue();
}