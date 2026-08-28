package com.kh.healthgate.consultation.model.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.consultation.model.dao.ConsultationDao;
import com.kh.healthgate.consultation.model.vo.Consultation;

@Service
public class ConsultationService {

	@Autowired
	private ConsultationDao consultationDao;
	
	// 전체 조회
	public List<Consultation> selectAllConsultation(LocalDate startDate, LocalDate endDate) {
		return consultationDao.selectAllConsultation(startDate, endDate);
	}
	
	public List<Consultation> selectConsultationByUserId(LocalDate startDate, LocalDate endDate, Long userId) {
		return consultationDao.selectConsultationByUserId(startDate, endDate, userId);
	}
	
	// 단건 조회
	public Consultation selectReservation(Long id) {
		return consultationDao.findById(id).orElse(null);
	}
	
	// 신청(등록)
	// 등록/수정 전 기존 데이터 조회
	public List<Consultation> reservationSelectByDate(LocalDate scheduledDate) {
		return consultationDao.reservationSelectByDate(scheduledDate);
	}
	
	// 등록
	@Transactional
	public Consultation saveConsultation(Consultation c) {
		return consultationDao.save(c);
	}
	
	// 예약 취소
	@Transactional
	public int deleteReservation(Long id) {
		return consultationDao.deleteReservation(id);
	}
	
}
