package com.kh.healthgate.consultation.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;

import com.kh.healthgate.consultation.model.service.ConsultationService;

@CrossOrigin
@RestController
public class ConsultationController {

	@Autowired
	private ConsultationService onsultationService;
	
	
}
