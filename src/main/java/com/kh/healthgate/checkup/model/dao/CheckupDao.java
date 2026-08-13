package com.kh.healthgate.checkup.model.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.checkup.model.vo.Checkup;

/**
 * 건강검진 기록의 DB 접근을 담당하는 Repository
 */
public interface CheckupDao extends JpaRepository<Checkup, Long> {

    /**
     * 해당 연도의 전체 건강검진 대상자 수 조회
     */
    long countByCheckupYear(Short checkupYear);

    /**
     * 해당 연도의 건강검진 완료자 수 조회
     */
    long countByCheckupYearAndCheckupDateIsNotNull(Short checkupYear);
    
    /**
     * 해당 연도의 건강검진 대상자 목록 조회
     * 검진 기록 식별자 오름차순으로 정렬한다.
     */
    List<Checkup> findByCheckupYearOrderByCheckupIdAsc(Short checkupYear);
}