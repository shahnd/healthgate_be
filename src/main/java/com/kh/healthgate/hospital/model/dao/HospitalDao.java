package com.kh.healthgate.hospital.model.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.hospital.model.vo.Hospital;

public interface HospitalDao extends JpaRepository<Hospital,Long>{
   
	
	// 병원 검색용+ 목록 조회용 쿼리메소드
	@Query("""
		    SELECT h 
		    FROM Hospital h
		    WHERE (:name IS NULL OR :name = '' OR h.name LIKE CONCAT('%', :name, '%'))
		      AND (:address IS NULL OR :address = '' OR h.address LIKE CONCAT('%', :address, '%'))
		      AND (:isGeneralExamAvailable IS NULL OR h.isGeneralExamAvailable = :isGeneralExamAvailable)
		      AND (:isStomachCancerExamAvailable IS NULL OR h.isStomachCancerExamAvailable = :isStomachCancerExamAvailable)
		      AND (:isColonCancerExamAvailable IS NULL OR h.isColonCancerExamAvailable = :isColonCancerExamAvailable)
		      AND (:isLiverCancerExamAvailable IS NULL OR h.isLiverCancerExamAvailable =:isLiverCancerExamAvailable)
		      AND (:isLungCancerExamAvailable IS NULL OR h.isLungCancerExamAvailable = :isLungCancerExamAvailable)
		    ORDER BY h.hospitalId DESC
		""")
		Page<Hospital> selectSearchList(
		    @Param("name") String name,
		    @Param("address") String address,
		    @Param("isGeneralExamAvailable") Boolean isGeneralExamAvailable,
		    @Param("isStomachCancerExamAvailable") Boolean isStomachCancerExamAvailable,
		    @Param("isColonCancerExamAvailable") Boolean isColonCancerExamAvailable,
		    @Param("isLiverCancerExamAvailable") Boolean isLiverCancerExamAvailable,
		    @Param("isLungCancerExamAvailable") Boolean isLungCancerExamAvailable,
		    Pageable pageable 
		);
	
    // 건강검진 병원 삭제 
	@Modifying
	@Query("""
			  DELETE Hospital h
			     WHERE h.hospitalId = :hospitalId
		   """)
	int deleteHospital(@Param("hospitalId") Long hospitalId);
}
