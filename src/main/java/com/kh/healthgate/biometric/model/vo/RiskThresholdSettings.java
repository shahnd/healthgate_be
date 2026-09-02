package com.kh.healthgate.biometric.model.vo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "risk_threshold_settings")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
@ToString
public class RiskThresholdSettings {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "metric_name", length = 30, nullable = false)
    private String metricName;

    @Column(name = "risk_level", length = 20, nullable = false)
    private String riskLevel;

    @Column(name = "threshold_value", nullable = false)
    private Float value;
}
