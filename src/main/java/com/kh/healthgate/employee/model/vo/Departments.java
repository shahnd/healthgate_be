package com.kh.healthgate.employee.model.vo;


import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name = "departments")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Departments {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "name", length = 30)
    private String  name;
}
