package com.kh.healthgate.checkup.model.dao;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.checkup.model.vo.Checkup;
import com.kh.healthgate.employee.model.vo.Employee;

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
    long countByCheckupYearAndCheckupDateIsNotNull(
            Short checkupYear
    );

    /**
     * 해당 연도의 건강검진 대상자 목록 조회
     *
     * 검진 기록 식별자 오름차순으로 정렬한다.
     */
    List<Checkup> findByCheckupYearOrderByCheckupIdAsc(
            Short checkupYear
    );

    /**
     * 특정 직원의 특정 연도 건강검진 기록 조회
     *
     * 같은 직원과 연도에 여러 기록이 존재할 경우
     * 가장 최근에 생성된 기록 한 건을 조회한다.
     *
     * Excel 업로드 시 기존 검진 기록이 있는지
     * 확인하기 위해 사용한다.
     */
    Optional<Checkup>
            findFirstByEmployeeAndCheckupYearOrderByCheckupIdDesc(
                    Employee employee,
                    Short checkupYear
            );

    /**
     * 특정 연도의 건강검진 미완료자 목록 조회
     *
     * 검진일이 null인 대상자는 미수검자로 판단한다.
     * 자동 알림 발송 대상자를 조회할 때 사용한다.
     */
    List<Checkup> findByCheckupYearAndCheckupDateIsNull(
            Short checkupYear
    );
    
    Optional<Checkup> findByEmployee_IdAndCheckupYear(
            Long employeeId,
            Short checkupYear
    );
}