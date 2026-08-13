package com.kh.healthgate.checkup.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.checkup.model.vo.CheckupReminder;

import java.util.List;

/**
 * 건강검진 알림 발송 이력의 DB 접근을 담당하는 Repository
 */
public interface CheckupReminderDao
        extends JpaRepository<CheckupReminder, Long> {
	
	/**
	 * 알림 발송 이력을 최신 발송 순으로 조회한다.
	 */
	List<CheckupReminder>
	        findAllByOrderByCheckupReminderSentAtDesc();

}