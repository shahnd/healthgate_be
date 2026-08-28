package com.kh.healthgate.consultation.model.dao;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.consultation.model.vo.Consultation;

public interface ConsultationDao extends JpaRepository<Consultation, Long>{

	
	// 예약 전체 조회
	// > 조회할 연월 일치, 정렬기준 날짜 DESC + 차시 ASC
	@Query("""
				SELECT c
				  FROM Consultation c
				 WHERE c.scheduledDate >= :startDate
				   AND c.scheduledDate < :endDate
				   AND c.status != 'CANCELED'
				 ORDER BY c.scheduledDate DESC,
						  c.scheduledTurn ASC
			""")
	List<Consultation> selectAllConsultation(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
	
	@Query("""
			SELECT c
			  FROM Consultation c
			 WHERE c.status != 'CANCELED'
			   AND c.scheduledDate >= :startDate
			   AND c.scheduledDate < :endDate
			   AND c.employee.id = :userId
			 ORDER BY c.scheduledDate DESC
				 	, c.scheduledTurn ASC
			""")
	List<Consultation> selectConsultationByUserId(@Param("startDate") LocalDate startMonth,
			 									 @Param("endDate")LocalDate endMonth,
			 									 @Param("userId") Long userId);
	
	// 예약 신청
	// 신청 가능 차시 조회
	// > 예약일자 + 예약순번 일치하는 행이 없을 때
	//	 또는 예약일자 일치, 예약순번 일치, 스테이터스 = EXCEL 일때

	@Query("""
			SELECT c
			  FROM Consultation c
			 WHERE c.scheduledDate = :scheduledDate
			   AND c.status != 'CANCELED'
			""")
	List<Consultation> reservationSelectByDate(@Param("scheduledDate") LocalDate scheduledDate);
	
	
	
	// 예약 단건 조회
	// > findById	
	
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
	int deleteReservation(@Param("id") Long id);
	
}
