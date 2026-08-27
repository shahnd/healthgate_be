package com.kh.healthgate.biometric.model.dao;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.biometric.model.vo.RiskThresholdSettings;

public interface RiskThresholdSettingDao extends JpaRepository<RiskThresholdSettings, Long>{

    Optional<RiskThresholdSettings> findByMetricNameAndRiskLevel(String metricName, String riskLevel);

}
