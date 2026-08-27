package com.kh.healthgate.biometric.model.service;

import org.springframework.stereotype.Service;

import com.kh.healthgate.biometric.model.dao.RiskThresholdSettingDao;
import com.kh.healthgate.biometric.model.vo.RiskThresholdSettings;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiskThresholdService {

    private final RiskThresholdSettingDao dao;

    public float getThreshold(String metricName, String riskLevel) {
        return dao.findByMetricNameAndRiskLevel(metricName, riskLevel)
                    .map(RiskThresholdSettings::getValue)
                    .orElseThrow(() -> new IllegalArgumentException(metricName + "/" + riskLevel + " 임계값 없음"));
    }

}
