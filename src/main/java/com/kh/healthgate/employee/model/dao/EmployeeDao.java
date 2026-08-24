package com.kh.healthgate.employee.model.dao;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.kh.healthgate.employee.model.vo.Employee;

/**
 * 직원 정보의 DB 접근을 담당하는 Repository
 */
public interface EmployeeDao
        extends JpaRepository<Employee, Long>,
                JpaSpecificationExecutor<Employee> {

    /**
     * 직원 상태에 따른 직원 목록 조회
     */
    Page<Employee> findByStatus(
            String status,
            Pageable pageable
    );

    /**
     * 사번으로 재직 중인 직원 한 명 조회
     *
     * Excel 건강검진 결과 업로드 시
     * Excel에 입력된 사번과 직원을 연결하기 위해 사용한다.
     */
    Optional<Employee> findByEmployeeNumberAndStatus(
            String employeeNumber,
            String status
    );
}