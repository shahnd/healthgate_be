package com.kh.healthgate.notice.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
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
@Table(name="notice")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Notice {
 
	@Id
	@Column(name="notice_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long noticeId;   
	
	@Column(name="title", nullable=false, length=255)
	private String title; // VARCHAR(255)  
	
	@Column(name="content", nullable=false, columnDefinition="TEXT")
	private String content;   //TEXT
	
	@Column(name="status", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status; // VARCHAR(20)
	
	@Column(name="created_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
	
	@Column(name="update_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
	
	// @Column(name="author_id", nullable=false)
	// private int authorId; 
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="author_id", nullable=false)
	private Employee employee;   // 작성자 식별자
	
	@Column(name="count", columnDefinition="INT DEFAULT 0")
	private int count;	         // 공지사항 조회수 
}
