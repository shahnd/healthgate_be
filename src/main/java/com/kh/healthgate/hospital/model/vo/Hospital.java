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
@Table(name="hospitals")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Hospital {
    
	@Id
	@Column(name="hospital_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long hospitalId; 
	
	@Column(name="name", nullable=false, length=255)
	private String name; // VARCHAR(255)
	
	@Column(name="address", nullable=false, length=500)
	private String address; // VARCHAR(500)
	
	@Column(name="phone", length=20)
	private String phone; // VARCHAR(20)
	
	@Column(name="url", length=255)
	private String url; // VARCHAR(255)
	
	@Column(name="description", columnDefinition="TEXT")
	private String description; // TEXT
	
	@Column(name="is_general_exam_available", nullable=false )
	private boolean isGeneralExamAvailable;
	
	@Column(name="is_stomach_cancer_exam_available", nullable=false)
	private boolean isStomachCancerExamAvailable;
	
	@Column(name="is_colon_cancer_exam_available", nullable=false)
	private boolean isColonCancerExamAvailable;
	
	@Column(name="is_liver_cancer_exam_available", nullable=false)
	private boolean isLiverCancerExamAvailable;
	
	@Column(name="is_lung_cancer_exam_available", nullable=false)
	private boolean isLungCancerExamAvailable;
	
	@Column(name="created_at", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;

}
