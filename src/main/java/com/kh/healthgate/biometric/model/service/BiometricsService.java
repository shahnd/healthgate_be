package com.kh.healthgate.biometric.model.service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.attendance.model.dao.AttendanceDao;
import com.kh.healthgate.attendance.model.service.AttendanceService;
import com.kh.healthgate.biometric.controller.BiometricsController.BiometricsInput;
import com.kh.healthgate.biometric.model.dao.BiometricsDao;
import com.kh.healthgate.biometric.model.vo.Biometrics;
import com.kh.healthgate.employee.model.dao.EmployeeDao;
import com.kh.healthgate.employee.model.vo.Employee;
import jakarta.transaction.Transactional;

@Service
public class BiometricsService {

    @Autowired
    private BiometricsDao biometricsDao;

    @Autowired
    private EmployeeDao employeeDao;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private RiskThresholdService riskThresholdService;


    public List<Biometrics> selectBiometricsList(Long employeeId, LocalDateTime startDate, LocalDateTime endDate) {
        return biometricsDao.findDailyLastByEmployeeIdAndDateRange(employeeId, startDate, endDate);
    }


    @Transactional
    public Biometrics insertBiometrics(BiometricsInput b) {

        float sysHigh = riskThresholdService.getThreshold("SYSTOLIC_BP", "HIGH");
        float sysWarn = riskThresholdService.getThreshold("SYSTOLIC_BP", "WARN");
        float diaHigh = riskThresholdService.getThreshold("DIASTOLIC_BP", "HIGH");
        float diaWarn = riskThresholdService.getThreshold("DIASTOLIC_BP", "HIGH");

        //최저 혈압 수치 판정
        String riskLevel;
        if (b.systolicBp() >= sysHigh || b.diastolicBp() >= diaHigh) {
            riskLevel = "HIGH";
        } else if(b.systolicBp() >= sysWarn || b.diastolicBp() >= diaWarn) {
            riskLevel = "WARN";
        } else {
            riskLevel = "NORMAL";
        }

        Employee employee = employeeDao.findById(b.employeeId()).orElse(null);

        Biometrics biometrics = new Biometrics();
        biometrics.setMeasuredAt(b.measuredAt());
        biometrics.setSystolicBp(b.systolicBp());
        biometrics.setDiastolicBp(b.diastolicBp());
        biometrics.setTemperature(b.temperature());
        biometrics.setHeartRate(b.heartRate());
        biometrics.setRiskLevel(riskLevel);
        biometrics.setEmployee(employee);

        if (riskLevel.equals("HIGH")) {
            attendanceService.insertAttendance("DENY", employee.getId());
        } else if (riskLevel.equals("WARN")) {
            attendanceService.insertAttendance("WARNING", employee.getId());
        } else {
            attendanceService.insertAttendance("ATTENDANCE", employee.getId());
        }



        return biometricsDao.save(biometrics);
    }


}
