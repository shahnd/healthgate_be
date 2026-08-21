package com.kh.healthgate.biometric.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.biometric.model.vo.Biometrics;

public interface BiometricsDao extends JpaRepository<Biometrics, Long> {

	List<Biometrics> findByEmployeeId(Long employeeId);

}
