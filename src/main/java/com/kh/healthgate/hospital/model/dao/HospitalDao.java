package com.kh.healthgate.hospital.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.hospital.model.vo.Hospital;

public interface HospitalDao extends JpaRepository<Hospital,Integer>{
   
	// 병원 목록 조회용 쿼리메소드
	// > SELECT * FROM HOSPITAL  
	//   ORDER BY ID DESC 
	Page<Hospital> findOrderByIdDesc(Pageable pageable);
	
	// 병원 검색용 쿼리메소드
	// > SELECT * FORM HOSPITAL
	//    WHERE NAME LIKE '%' || ? || '%'
	//      AND ADDRESS LIKE '%' || ? || '%'   
	//      AND IS_GENERAL_EXAM_AVAILABLE = '%' || ? || '%'
	//      AND IS_STOMACH_CANCEL_EXAM_AVAILABLE = '%' || ? || '%'
	//      AND IS_COLON_CANCER_EXAM_AVILABLE = '%' || ? || '%'
	//      AND IS_LIVER_CANCER_EXAM_AVAILABLE = '%' || ? || '%'
	//      AND IS_LUNG_CANCER_EXAM_AVAILABLE = '%' || ? || '%'
    //  ORDER BY ID DESC 
	
	Page<Hospital> findByNameContainingAndAddressContainingAndIsStomathCancelExamAvilableContainingAndIsColonCancerExamAvilableContainingAndIsLiverCancerExamAvilableContainingAndIsLungCancerExamAvilableContainingOrderByIdDesc(String keyword,Pageable pageable);      

    // 건강검진 병원 삭제 
	@Modifying
	@Query("""
			  DELETE Hospital h
			     WHERE h.id = :id
		   """)
	int deleteHospital(@Param("id") int id);
}
