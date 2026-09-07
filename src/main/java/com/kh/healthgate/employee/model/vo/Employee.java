package com.kh.healthgate.employee.model.vo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.kh.healthgate.attendance.model.vo.Timecards;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "employees")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"timecards"})
public class Employee {

    @Id
    @Column(name = "id", nullable=false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_number", nullable = false, length = 100, unique = true)
    private String employeeNumber;

    @Column(name = "password", nullable = false, length = 100)
    private String password;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "hire_date", nullable = false)
    private LocalDate hireDate;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 13)
    private String phone;

    @Enumerated(EnumType.STRING)
    @ColumnDefault(value = "'EMPLOYEE'")
    @Column(name = "role", nullable = false)
    private EmployeeRole role = EmployeeRole.EMPLOYEE;

    @ColumnDefault(value = "'Y'")
    @Column(name = "status", nullable = false, length = 1)
    private String status = "Y";

    @Column(name = "created_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime updatedAt = LocalDateTime.now();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="department_id", nullable = false)
    private Departments departments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id", nullable = false)
    private Positions positions;

    @OneToMany(mappedBy = "employee", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Timecards> timecards = new ArrayList<>();


    public void changeStatusToInactive() {
        this.status = "N";
    }
}
