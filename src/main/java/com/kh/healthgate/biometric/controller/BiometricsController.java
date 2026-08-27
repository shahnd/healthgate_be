package com.kh.healthgate.biometric.controller;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.biometric.model.service.BiometricsService;
import com.kh.healthgate.biometric.model.vo.Biometrics;
import com.kh.healthgate.common.model.vo.ApiResponse;

@CrossOrigin
@RestController
public class BiometricsController {

    @Autowired
    private BiometricsService biometricsService;

    public record BiometricsInput(
        LocalDateTime measuredAt,
        Integer systolicBp,
        Integer diastolicBp,
        Float temperature,
        Integer heartRate,
        Long employeeId
    ) {}

    @GetMapping("/biometrics/{employeeId}")
    public ResponseEntity<ApiResponse<List<Biometrics>>> selectBiometricsList(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "1") int months) {

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        List<Biometrics> list = biometricsService.selectBiometricsList(employeeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/biometrics")
    public ResponseEntity<ApiResponse<Void>> insertBiometrics(@RequestBody BiometricsInput b) {

        biometricsService.insertBiometrics(b);

        return ResponseEntity.ok(ApiResponse.successWithNoData("success"));
    }



    

}
