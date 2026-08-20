package com.kh.healthgate.consultation.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.consultation.model.service.ConsultationService;
import com.kh.healthgate.consultation.model.vo.Consultation;

@CrossOrigin
@RestController
@RequestMapping("healthgate")
public class ConsultationController {

	@Autowired
	private ConsultationService consultationService;
	
	// 예약 전체 조회(캘린더 형식, 선택된 월별)
	@GetMapping("reservations/list")
	public ResponseEntity<List<Consultation>> selectAllReservation(@RequestParam LocalDate consultationScheduledDate) {
		
		// 프론트엔드에서 넘어왔다고 가정하는 Date 객체 생성 (예: 2026년 6월 15일)
		// LocalDate consultationScheduledDate = LocalDate.of(2026, 6, 15);
		
        
		// 전체 조회
		// 예약일에서 각각 연, 월로 추출 후 서비스로 넘기기
        int year = consultationScheduledDate.getYear();
        int month = consultationScheduledDate.getMonthValue();

        // 디버깅
        System.out.println("원본 LocalDate : " + consultationScheduledDate);
        System.out.println("추출된 연도 : " + year);
        System.out.println("추출된 월 : " + month);
		
        // 서비스 호출
		List<Consultation> list = consultationService.selectAllReservation(year, month);
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
		
	} //selectAllReservation
	
	
	// 예약 단건 조회
	@GetMapping("reservations/details/{consultationId}")
	public ResponseEntity<Consultation> selectReservation(@PathVariable Long consultationId) {
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(consultationId);
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectReservation
	
	
	// 예약 신청/수정을 위한 조회
	@GetMapping("reservations/views")
	public ResponseEntity<Consultation> reservationSelectByDate(/* @RequestParam LocalDate consultationScheduledDate */) {
		
		// > 프론트에서 캘린더 변동이 있을 때 마다 새로 조회해야함.
		//	 조회 조건은 연,월,일 이므로 LocalDate 넘기면서 단건 조회
		
		// 예시 2026-06-15
		LocalDate consultationScheduledDate = LocalDate.of(2026, 6, 15);
        
        Consultation c = consultationService.reservationSelectByDate(consultationScheduledDate);
        
        return ResponseEntity.status(HttpStatus.OK)
        					 .body(c);
	}
	
	
	// 예약 신청
	@PostMapping("reservations")
	public ResponseEntity<String> insertReservation(@RequestBody Consultation c) {
		
		// 신청사유 XSS 방어

		//  insert
		Consultation result = consultationService.saveConsultation(c); 
		
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //insertReservation
	
	
	// 예약 수정
	@PutMapping("reservations/{consultationId}")
	public ResponseEntity<String> updateReservation(@PathVariable Long consultationId, @RequestBody Consultation c) {
		
		// 신청사유 XSS 방어
		
		// update
		Consultation result = consultationService.saveConsultation(c); 
		
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //updateReservation
	
	
	// 예약 취소
	@DeleteMapping("reservations/{consultationId}")
	public ResponseEntity<String> deleteReservation(@PathVariable Long consultationId) {
		
		// soft delete
		int result = consultationService.deleteReservation(consultationId);
		
		String msg = (result > 0) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //deleteReservation
	
	
	// ==================================
	
	
	// 상담 전체 조회 (리스트 형식)
	@GetMapping("consultations/list")
	public ResponseEntity<List<Consultation>> selectAllConsultation() {
		
		// 조회
		List<Consultation> list = consultationService.selectAllConsultation();
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(list);
	} //selectAllConsultation
	
	
	// 상담 단건 조회
	@GetMapping("consultations/detail/{consultationId}")
	public ResponseEntity<Consultation> selectConsultation(@PathVariable Long consultationId) {
		
		// 예약번호와 일치한 행 조회
		Consultation c = consultationService.selectReservation(consultationId);
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(c);
	} //selectConsultation
	
	
	// 상담 일지 작성/수정
	@PutMapping("consultations/{consultationId}")
	public ResponseEntity<String> saveConsultation(@PathVariable Long consultationId, @RequestBody Consultation c) {
		
		// 상담 내용 XSS 방어
		
		// insert(update)
		Consultation result = consultationService.saveConsultation(c); 
		String msg = (result != null) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //insertConsultation

}
