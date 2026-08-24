package com.kh.healthgate.attendance.model.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.attendance.model.dao.AttendanceDao;
import com.kh.healthgate.attendance.model.vo.Timecards;
import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.transaction.Transactional;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceDao attendanceDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Transactional
    public void insertAttendance(String status, Long employeeId) {

        LocalDateTime startOfDay = LocalDate.now().atStartOfDay();
        LocalDateTime endOfDay = LocalDate.now().atTime(LocalTime.MAX);

        Optional<Timecards> timecardsOpt = attendanceDao.findByEmployeeIdAndClockInAtBetween(employeeId, startOfDay, endOfDay);

        
        if (timecardsOpt.isPresent() && "ATTENDANCE".equals(timecardsOpt.get().getStatus())) {
            return;
        }

        if (timecardsOpt.isPresent()) {
            Timecards denyTimecards = timecardsOpt.get();
            denyTimecards.setStatus(status);
            denyTimecards.setClockInAt(LocalDateTime.now());

        } else {
            Timecards timecards = new Timecards();
            timecards.setStatus(status);
            Employee employeeProxy = employeeDao.getReferenceById(employeeId);
            timecards.setEmployee(employeeProxy);
            attendanceDao.save(timecards);
        }
        
        



        return;
    }

}
