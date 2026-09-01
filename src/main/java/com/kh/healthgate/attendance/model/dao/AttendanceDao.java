package com.kh.healthgate.attendance.model.dao;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.attendance.model.vo.Timecards;

public interface AttendanceDao extends JpaRepository<Timecards, Long>{

    Optional<Timecards> findByEmployeeIdAndClockInAtBetween(Long employeeId, LocalDateTime startOfDay,
            LocalDateTime endOfDay);

    long countByStatusAndClockInAtBetween(String string, LocalDateTime start, LocalDateTime end);

}
