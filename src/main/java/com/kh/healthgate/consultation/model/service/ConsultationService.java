package com.kh.healthgate.consultation.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.consultation.model.dao.ConsultationDao;

@Service
public class ConsultationService {

	@Autowired
	private ConsultationDao consultationDao;

	@Transactional
	public int deleteReservation(Long consultationId) {
		return consultationDao.deleteReservation(consultationId);
	}
	
	
}
