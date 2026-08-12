package com.kh.healthgate.consultation.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.consultation.model.vo.Consultation;

public interface ConsultationDao extends JpaRepository<Consultation, Long>{

	
	// 예약 전체 조회
	// > 조회할 연월 일치 - 보건관리자 : 전체조회
	//					근로자 : EMPLOYEE_ID 일치할 때
	// > 정렬기준 최신순 (차시 asc)
	// > JPQL은 참고할 예시
	@Query("""
			SELECT c
			  FROM Consultation c
	         WHERE FUNCTION('DATE_FORMAT', c.consultationScheduledDate, '%Y-%m') = :yearMonth
			   AND (:isAdmin = true OR c.employee.employeeId = :employeeId)
			 ORDER BY c.consultationScheduledDate DESC,
			   		  c.consultationScheduledTurn ASC
			""")
	    List<Consultation> findConsultationsByRoleAndYearMonth(
	            @Param("yearMonth") String yearMonth,      // 예: "2026-09"
	            @Param("isAdmin") boolean isAdmin,         // 보건관리자 여부 (true/false)
	            @Param("employeeId") Long employeeId       // 일반 근로자 ID (관리자일 경우 무시됨)
	    );
	
	
	// 예약 신청
	// > 예약일자 + 예약순번 일치하는 행이 없을 때
	//	 또는 예약일자 일치, 예약순번 일치, 스테이터스 = EXCEL 일때
	
	// 예약 단건 조회
	// > CONSULTATION_ID 일치 - 근로자는 EMPLOYEE_ID 일치할 때만
	
	// 예약 수정
	// > 예약신청조건 동일
	
	// 예약 일정 소프트 삭제
	@Modifying
	@Query("""
			UPDATE Consultation c 
			   SET c.consultationStatus = 'N'
			 WHERE c.consultationId = :consultationId
			   AND c.consultationStatus = 'Y'
			""")
	int deleteReservation(@Param("consultationId") Long consultationId);

	
	// ========================================
	
	// 상담 전체 조회
	
	
	// 상담 단건 조회
	// > CONSULTATION_ID 일치 - 근로자는 EMPLOYEE_ID 일치할 때만
	
	// 상담 일지 작성
	
	// 상담 일지 수정
	
	
}
