package com.kh.healthgate.biometric.model.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public List<Biometrics> selectBiometricsList(Long employeeId) {
        return biometricsDao.findByEmployeeId(employeeId);
    }


    @Transactional
    public Biometrics insertBiometrics(BiometricsInput b) {

        //최저 혈압 수치 판정
        String riskLevel;
        if (b.systolicBp() >= 140 || b.diastolicBp() >= 90) {
            riskLevel = "HIGH";
        } else if(b.systolicBp() >= 130 || b.diastolicBp() >= 80) {
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



        return biometricsDao.save(biometrics);
    }


}
