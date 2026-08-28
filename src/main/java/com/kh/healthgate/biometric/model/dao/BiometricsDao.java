package com.kh.healthgate.biometric.model.dao;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.biometric.model.vo.Biometrics;

public interface BiometricsDao extends JpaRepository<Biometrics, Long> {

	List<Biometrics> findByEmployeeId(Long employeeId);

	@Query(value = """
		SELECT * FROM (
			SELECT b.*,
				ROW_NUMBER() OVER (
					PARTITION BY DATE(b.measured_at)
					ORDER BY b.measured_at DESC
				) AS rn
			FROM biometrics b
			WHERE b.employee_id = :employeeId
			AND b.measured_at BETWEEN :startDate AND :endDate
		) t
		WHERE t.rn = 1
		ORDER BY t.measured_at ASC
		""", nativeQuery = true)
	List<Biometrics> findDailyLastByEmployeeIdAndDateRange(
			@Param("employeeId") Long employeeId,
			@Param("startDate") LocalDateTime startDate,
			@Param("endDate") LocalDateTime endDate
	);
}
