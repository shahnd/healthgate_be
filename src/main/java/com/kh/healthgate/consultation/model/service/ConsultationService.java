package com.kh.healthgate.consultation.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.kh.healthgate.consultation.model.dao.ConsultationDao;

@Service
public class ConsultationService {

	@Autowired
	private ConsultationDao consultationDao;
	
	
}
