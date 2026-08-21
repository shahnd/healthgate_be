package com.kh.healthgate.biometric.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "biometrics")
@DynamicInsert
@DynamicUpdate
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = "employee")
public class Biometrics {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measured_at", nullable = false)
    private LocalDateTime measuredAt;

    @Column(name = "systolic_bp", nullable = false)
    private Integer systolicBp;

    @Column(name = "diastolic_bp", nullable = false)
    private Integer diastolicBp;

    @Column(name = "temperature")
    private Float temperature;

    @Column(name = "heart_rate", nullable = false)
    private Integer heartRate;

    @Column(name = "risk_level", length = 20, nullable = false)
    private String riskLevel;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

}
