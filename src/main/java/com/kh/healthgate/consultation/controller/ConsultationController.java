package com.kh.healthgate.consultation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.consultation.model.service.ConsultationService;

@CrossOrigin
@RestController
public class ConsultationController {

	@Autowired
	private ConsultationService consultationService;
	
	// 예약 전체 조회
	public void selectAllReservation() {
		
		// 전체 조회
		
		// 결과 반환
		
	} //selectAllReservation
	
	// 예약 신청
	public void insertReservation() {
		
		// 1. 예약이 가능한 일자와 차시를 판벌하기 위해 전체 조회 먼저 실시
		selectAllReservation();
		
		// 2. 일자와 차시를 선택하고 신청 시 insert
		
		
		// 결과 반환
		
		
	} //insertReservation
	
	
	// 예약 단건 조회
	public void selectReservation() {
		
		// 예약번호와 일치한 행 조회
		
		// 결과 반환
		
	} //selectReservation
	
	
	// 예약 수정
	public void updateReservation() {
		
		// 예약 단건 조회
		selectReservation();
		
		// update
		
		// 결과 반환
		
		
	} //updateReservation
	
	// 예약 취소
	public ResponseEntity<String> deleteReservation(Long consultationId) {
		
		// soft delete
		int result = consultationService.deleteReservation(consultationId);
		
		String msg = (result > 0) ? "success" : "fail";
		
		// 결과 반환
		return ResponseEntity.status(HttpStatus.OK)
							 .body(msg);
	} //deleteReservation
	
	
	// ==================================
	
	// 상담 전체 조회
	public void selectAllConsultation() {
		
	} //selectAllConsultation
	
	// 상담 단건 조회
	public void selectConsultation() {
		
	} //selectConsultation
	
	// 상담 일지 작성
	public void insertConsultation() {
		
	} //insertConsultation
	
	// 상담 일지 수정
	public void updateConsultation() {
		
	} //updateConsultation
	
}
