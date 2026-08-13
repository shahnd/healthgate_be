package com.kh.healthgate.checkup.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.checkup.model.vo.CheckupReminderSetting;

/**
 * 건강검진 자동 알림 설정의 DB 접근을 담당하는 Repository
 */
public interface CheckupReminderSettingDao
        extends JpaRepository<CheckupReminderSetting, Long> {

}