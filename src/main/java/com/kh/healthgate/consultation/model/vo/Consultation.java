package com.kh.healthgate.consultation.model.vo;

import java.time.LocalDateTime;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="HEALTH_CONSULTATIONS")
@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
public class Consultation {

	@Id
	@Column(name="CONSULTATION_ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long consultationId;		// consultations_id	상담예약번호(ID)
	
	@ManyToOne
	@JoinColumn(name="EMPLOYEE_ID", nullable=false)
	private Employee employee;		// employee	근로자(FK, NN)
	
	@ManyToOne
	@JoinColumn(name="MANAGER_ID", nullable=false)
	private Employee manager;			// manager	상담사(FK, NN)
	
	@Column(name="CONSULTATION_SCHEDULED_DATE", nullable=false,
			columnDefinition="DATE")
	private Date consultationScheduledDate;			// scheduled_date	예약일자(NN, DATE)
	
	@Column(name="CONSULTATION_SCHEDULED_TURN", nullable=false,
			columnDefinition="CHAR(2)")
	private String consultationScheduledTurn;		// scheduled_turn	예약순번(NN, CHAR(2) 'T1'/ 'T2/ ... / 'T6'(1차 ~ 6차)
	
	@Column(name="CONSULTATION_REASON", nullable=false, length=100)
	private String consultationReason;				// reason	신청사유(NN, VARCHAR(100))
	
	@Column(name="CONSULTATION_CONTENT",
			columnDefinition="TEXT")
	private String consultationContent;				// content	상담내용(NN, TEXT)
	
	@Enumerated(EnumType.STRING)
	@Column(name="CONSULTATION_STATUS", nullable=false, length=20,
			columnDefinition="VARCHAR(20) DEFAULT 'RESERVED'")
	private ConsultationStatus consultationStatus;				// status	상담진행상태(NN), DEFAULT RESERVED
	
	@Column(name="CONSULTATION_CONSULTATED_AT", columnDefinition="DATETIME")
	private LocalDateTime consultationConsultatedAt;		// requested_at	일지작성일시(CURRENT_TIMESTAMP)
	
	@Column(name="CREATED_AT", nullable=false,
			columnDefinition="DATETIME DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime consultationCreatedAt;		// logged_at	상담예약신청일시(CURRENT_TIMESTAMP)
}
