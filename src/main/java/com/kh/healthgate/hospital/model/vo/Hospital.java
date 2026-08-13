package com.kh.healthgate.hospital.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="HOSPITAL")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Hospital {
    
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id; 
	
	@Column(name="NAME", nullable=false, length=255)
	private String name; // VARCHAR(255)
	
	@Column(name="ADDRESS", nullable=false, length=500)
	private String address; // VARCHAR(500)
	
	@Column(name="PHONE", length=20)
	private String phone; // VARCHAR(20)
	
	@Column(name="URL", length=255)
	private String url; // VARCHAR(255)
	
	@Column(name="DESCIPTION", columnDefinition="TEXT")
	private String description; // TEXT
	
	@Column(name="IS_GENERAL_EXAM_AVAILABLE", nullable=false)
	private boolean isGeneralExamAvailable;
	
	@Column(name="IS_STOMACH_CANCEL_EXAM_AVAILABLE", nullable=false)
	private boolean isStomachCancelExamAvailable;
	
	@Column(name="IS_COLON_CANCER_EXAM_AVAILABLE", nullable=false)
	private boolean isColonCancerExamAvailable;
	
	@Column(name="IS_LIVER_CANCER_EXAM_AVAILABLE", nullable=false)
	private boolean isLiverCancerExamAvailable;
	
	@Column(name="IS_LUNG_CANCER_EXAM_AVAILABLE", nullable=false)
	private boolean isLungCancerExamAvailable;
	
	@Column(name="CREATED_AT", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime CreatedAt;

}
