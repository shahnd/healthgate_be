package com.kh.healthgate.biometric.controller;

import com.kh.healthgate.biometric.model.service.RiskThresholdService;
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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.biometric.model.service.BiometricsService;
import com.kh.healthgate.biometric.model.vo.Biometrics;
import com.kh.healthgate.biometric.model.vo.RiskThresholdSettings;
import com.kh.healthgate.common.model.vo.ApiResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@CrossOrigin
@RestController
@Tag(name = "생체 정보 API", description = "생체 정보 등록, 조회")
public class BiometricsController {

    public record RiskDTO(
        Long id,
        float value
    ) {}

    private final RiskThresholdService riskThresholdService;

    @Autowired
    private BiometricsService biometricsService;

    BiometricsController(RiskThresholdService riskThresholdService) {
        this.riskThresholdService = riskThresholdService;
    }

    public record BiometricsInput(
        LocalDateTime measuredAt,
        Integer systolicBp,
        Integer diastolicBp,
        Float temperature,
        Integer heartRate,
        Long employeeId
    ) {}

    @GetMapping("/biometrics/{employeeId}")
    @Operation(summary = "직원 생체 정보 조회", description = "직원 ID를 통해 생체 정보를 조회합니다.")
    public ResponseEntity<ApiResponse<List<Biometrics>>> selectBiometricsList(
            @PathVariable Long employeeId,
            @RequestParam(defaultValue = "1") int months) {

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusMonths(months);

        List<Biometrics> list = biometricsService.selectBiometricsList(employeeId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success(list));
    }

    @PostMapping("/biometrics")
    @Operation(summary = "생체 정보 등록", description = "생체 정보를 등록합니다.")
    public ResponseEntity<ApiResponse<Void>> insertBiometrics(@RequestBody BiometricsInput b) {

        biometricsService.insertBiometrics(b);

        return ResponseEntity.ok(ApiResponse.successWithNoData("success"));
    }

    @PutMapping("/risks")
    @Operation(summary = "임계점 수정", description = "임계점 설정 수치를 수정합니다.")
    public ResponseEntity<ApiResponse<Void>> updateRiskThresholds(@RequestBody List<RiskDTO> requests) {

        riskThresholdService.updateThreshold(requests);

        return ResponseEntity.ok(ApiResponse.successWithNoData("success"));
    }

    @GetMapping("/risks")
    @Operation(summary = "임계점 조회", description = "임계점 설정 수치를 조회합니다.")
    public ResponseEntity<ApiResponse<List<RiskThresholdSettings>>> getRiskThresholds() {
        List<RiskThresholdSettings> list = riskThresholdService.getRiskThresholds();

        System.out.println(list);

        return ResponseEntity.ok(ApiResponse.success(list));
    }
    



    

}
