package com.kh.healthgate.employee.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employees") // 실제 DB 테이블명과 매칭
@NoArgsConstructor
@Getter
@Setter
@ToString
public class Employee {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // 식별자 (필수)

    @Column(name = "name", nullable = false)
    private String name; // 상담 내용 매핑 시 직원 이름(item.employee.name)을 쓰기 때문에 name은 필수로 있어야 합니다!
}