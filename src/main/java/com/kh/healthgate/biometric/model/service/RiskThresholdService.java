package com.kh.healthgate.biometric.model.service;

import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import com.kh.healthgate.biometric.controller.BiometricsController.RiskDTO;
import com.kh.healthgate.biometric.model.dao.RiskThresholdSettingDao;
import com.kh.healthgate.biometric.model.vo.RiskThresholdSettings;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RiskThresholdService {

    private final RiskThresholdSettingDao dao;

    @Cacheable(value = "riskThresholds", key = "#metricName + ':' + #riskLevel")
    public float getThreshold(String metricName, String riskLevel) {
        return dao.findByMetricNameAndRiskLevel(metricName, riskLevel)
                    .map(RiskThresholdSettings::getValue)
                    .orElseThrow(() -> new IllegalArgumentException(metricName + "/" + riskLevel + " 임계값 없음"));
    }

    @Transactional
    @CacheEvict(value = "riskThresholds", allEntries = true)
    public void updateThreshold(List<RiskDTO> requests) {
        for (RiskDTO r: requests) {
            RiskThresholdSettings entity = dao.findById(r.id()).orElseThrow(() -> new IllegalArgumentException("id " + r.id() + " 임계값 없음"));
            entity.setValue(r.value());
            dao.save(entity);
        }
    }

    public List<RiskThresholdSettings> getRiskThresholds() {
        return dao.findAll();
    }

}
