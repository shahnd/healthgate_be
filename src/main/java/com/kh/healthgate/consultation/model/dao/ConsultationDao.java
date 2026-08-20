package com.kh.healthgate.consultation.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.consultation.model.vo.Consultation;

public interface ConsultationDao extends JpaRepository<Consultation, Long>{

	
	// 예약 전체 조회
	// > 조회할 연월 일치, 정렬기준 날짜 DESC + 차시 ASC
	/*
	 	권한 유무 상관 없이 전체 조회 (FE 에서 권한에 따라 구분할 예정)
	 	SELECT *
	 	  FROM CONSULTATIONS
	 	 WHERE YEAR(SCHEDULED_DATE) = ?
	 	   AND MONTH(SCHEDULED_DATE) = ?
	 	 ORDER BY SCHEDULED_DATE DESC,
	 	 		  SCHEDULED_TURN ASC
	 */
	@Query("""
				SELECT c
				  FROM Consultation c
				 WHERE YEAR(scheduledDate) = :year
				   AND MONTH(scheduledDate) = :month
				 ORDER BY c.scheduledDate DESC,
						  c.scheduledTurn ASC
			""")
	List<Consultation> selectAllReservation(@Param("year") int year, @Param("month") int month);
	
	
	// 예약 신청
	// 신청 가능 차시 조회
	// > 예약일자 + 예약순번 일치하는 행이 없을 때
	//	 또는 예약일자 일치, 예약순번 일치, 스테이터스 = EXCEL 일때
	Consultation findByScheduledDate(LocalDate scheduledDate);
	
	
	
	// 예약 단건 조회
	// > CONSULTATION_ID 일치
	@Query("""
			SELECT c
			  FROM Consultation c
			 WHERE c.id = :id
			""")
	Consultation selectReservation(@Param("id") Long id);
	
	
	// 예약 수정
	// > 예약신청조건 동일
	
	
	
	// 예약 일정 소프트 삭제
	@Modifying
	@Query("""
			UPDATE Consultation c 
			   SET c.status = 'CANCELED'
			 WHERE c.id = :id
			   AND c.status = 'RESERVED'
			""")
	int deleteReservation(@Param("consultationId") Long id);


	
	
	// ========================================
	
	// 상담 전체 조회
	// > 날짜 DESC + 차시 DESC
	// > 필터링 기본 적용으로 변경 예정
	// 기간, 이름, 상태
	@Query("""
			SELECT c
			  FROM Consultation c
			 ORDER BY c.scheduledDate DESC
				 	, c.scheduledTurn DESC
			""")
	List<Consultation> selectAllConsultation();


	
	
	// 상담 단건 조회
	/* 예약 단건 조회와 동일한 메소드 사용 */
	
	
	// 상담 일지 작성
	
	
	// 상담 일지 수정
	
	
}
