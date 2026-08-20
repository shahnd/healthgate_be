package com.kh.healthgate.consultation.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.consultation.model.dao.ConsultationDao;
import com.kh.healthgate.consultation.model.vo.Consultation;

@Service
public class ConsultationService {

	@Autowired
	private ConsultationDao consultationDao;
	
	// 예약 전체 조회
	public List<Consultation> selectAllReservation(int year, int month) {
		return consultationDao.selectAllReservation(year, month);
	}

	// 예약 단건 조회
	public Consultation selectReservation(Long id) {
		return consultationDao.selectReservation(id);
	}
	
	// 예약 신청(등록)
	// 조회
	public Consultation reservationSelectByDate(LocalDate scheduledDate) {
		return consultationDao.findByScheduledDate(scheduledDate);
	}
	
	// 등록
	@Transactional
	public Consultation saveConsultation(Consultation c) {
		return consultationDao.save(c);
	}
	
	
	// 예약 수정
	// 조회 - reservationSelectByDate
	// 수정 - selectReservation
	
	
	// 예약 취소
	@Transactional
	public int deleteReservation(Long id) {
		return consultationDao.deleteReservation(id);
	}
	
	// ================================================================ 
	
	// 상담 전체 조회
	public List<Consultation> selectAllConsultation() {
		return consultationDao.selectAllConsultation();
	}

	// 상담 단건 조회 - selectReservation
	
	// 상담 등록, 수정
	// 조회 - reservationSelectByDate
	// 등록, 수정 - saveReservation
	
}
