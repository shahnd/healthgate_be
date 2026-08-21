package com.kh.healthgate.biometric.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
    public ResponseEntity<ApiResponse<List<Biometrics>>> selectBiometricsList(@PathVariable Long employeeId) {
        
        List<Biometrics> list = biometricsService.selectBiometricsList(employeeId);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/biometrics")
    public ResponseEntity<ApiResponse<Void>> insertBiometrics(@RequestBody BiometricsInput b) {

        Biometrics biometrics = biometricsService.insertBiometrics(b);

        return ResponseEntity.ok(ApiResponse.successWithNoData("success"));
    }



    

}
