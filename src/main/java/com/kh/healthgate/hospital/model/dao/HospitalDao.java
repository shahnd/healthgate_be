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
		    WHERE (:keywordName IS NULL OR :keywordName = '' OR h.name LIKE CONCAT('%', :keywordName, '%'))
		      AND (:keywordAddress IS NULL OR :keywordAddress = '' OR h.address LIKE CONCAT('%', :keywordAddress, '%'))
		      AND (:isGeneral IS NULL OR :isGeneral = false OR h.isGeneralExamAvailable = :isGeneral)
		      AND (:isStomachCancer IS NULL OR :isStomachCancer = false OR h.isStomachCancerExamAvailable = :isStomachCancer)
		      AND (:isColonCancer IS NULL OR :isColonCancer = false OR h.isColonCancerExamAvailable = :isColonCancer)
		      AND (:isLiverCancer IS NULL OR :isLiverCancer = false OR h.isLiverCancerExamAvailable =:isLiverCancer)
		      AND (:isLungCancer IS NULL OR :isLungCancer = false OR h.isLungCancerExamAvailable = :isLungCancer)
		      AND h.status = 'Y'
		    ORDER BY h.hospitalId DESC
		""")
		Page<Hospital> selectSearchList(
		    @Param("keywordName") String keywordName,
		    @Param("keywordAddress") String keywordAddress,
		    @Param("isGeneral") Boolean isGeneral,
		    @Param("isStomachCancer") Boolean isStomachCancer,
		    @Param("isColonCancer") Boolean isColonCancer,
		    @Param("isLiverCancer") Boolean isLiverCancer,
		    @Param("isLungCancer") Boolean isLungCancer,
		    String status,
		    Pageable pageable 
		);
	
    // 건강검진 병원 삭제 
	@Modifying
	@Query("""
			  UPDATE Hospital h
			     SET h.status = 'N'
			   WHERE h.hospitalId = :hospitalId
			     AND h.status = 'Y'
		   """)
	int deleteHospital(@Param("hospitalId") Long hospitalId);
}
