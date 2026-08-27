package com.kh.healthgate.checkup.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.PrePersist;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "checkups")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
public class Checkup {

    /**
     * 건강검진 기록 식별자(PK)
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "checkup_id", nullable = false)
    private Long checkupId;

    /**
     * 건강검진 대상 연도
     * 예: 2026
     */
    @Column(name = "checkup_year", nullable = false)
    private Short checkupYear;

    /**
     * 실제 건강검진을 받은 날짜
     * 검진을 받지 않았다면 null
     */
    @Column(name = "checkup_date")
    private LocalDate checkupDate;

    /**
     * 건강검진 내용 또는 결과 요약
     */
    @Column(name = "checkup_summary", columnDefinition = "TEXT")
    private String checkupSummary;

    /**
     * 건강검진 기록 생성 일시
     * DB의 CURRENT_TIMESTAMP 기본값을 사용
     */
    @Column(
        name = "checkup_created_at",
        nullable = false,
        columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP"
    )
    private LocalDateTime checkupCreatedAt;

    /**
     * 건강검진 기록의 대상 직원
     *
     * 여러 건강검진 기록이 한 명의 직원을 참조하는
     * 다대일(N:1) 관계
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
    
    /**
     * 건강검진 기록이 처음 저장될 때 생성 일시를 자동 입력한다.
     */
    @PrePersist
    public void prePersist() {

        if (this.checkupCreatedAt == null) {
            this.checkupCreatedAt = LocalDateTime.now();
        }
    }
}