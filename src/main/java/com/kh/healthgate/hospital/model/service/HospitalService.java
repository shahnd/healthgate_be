package com.kh.healthgate.hospital.model.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.hospital.model.dao.HospitalDao;
import com.kh.healthgate.hospital.model.vo.Hospital;

@Service
public class HospitalService {

	@Autowired
	private HospitalDao hospitalDao;

	
	public Page<Hospital> selectHospitalList(Pageable pageable) {
		
		return hospitalDao.findByOrderByIdDesc(pageable);
	}

    /*
	public Page<Hospital> selectSearchList(String keyword, Pageable pageable) {
		
		 return hospitalDao.findByNameContainingAndAddressContainingAndIsStomathCancelExamAvilableContainingAndIsColonCancerExamAvilableContainingAndIsLiverCancerExamAvilableContainingAndIsLungCancerExamAvilableContainingOrderByIdDesc(keyword, pageable);
	}
     */
	
	public Hospital selectHospital(int id) {
		
		return hospitalDao.findById(id).orElse(null);
	}

    @Transactional
	public Hospital insertHospital(Hospital h) {
		
		return hospitalDao.save(h);
	}

    @Transactional
	public int deleteHospital(int id) {
		
		return hospitalDao.deleteHospital(id);
	}
}
