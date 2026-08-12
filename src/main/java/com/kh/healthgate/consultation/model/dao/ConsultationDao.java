package com.kh.healthgate.consultation.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.consultation.model.vo.Consultation;

public interface ConsultationDao extends JpaRepository<Consultation, Long>{

}
