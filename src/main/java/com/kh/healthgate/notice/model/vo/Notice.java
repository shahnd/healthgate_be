package com.kh.healthgate.notice.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import com.kh.healthgate.employee.model.vo.Employee;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Table(name="notices")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class Notice {
	
	@Schema(description="공지사항 번호 (자동 생성)", example="1",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Id
	@Column(name="notice_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long noticeId;   
	
	@Schema(description="공지사항 제목", example="여름철 주의사항 안내", 
			requiredMode=Schema.RequiredMode.REQUIRED)
	@Column(name="title", nullable=false, length=255)
	private String title; // VARCHAR(255)  
	
	@Schema(description="공지사항 내용", example="여름철 주의사항 입니다. 여름에는 안전사고가 잇따라 발생하니 첨부사항 확인안내부탁드립니다.",
			requiredMode=Schema.RequiredMode.REQUIRED)
	@Column(name="content", nullable=false, columnDefinition="TEXT")
	private String content;   //TEXT
	
	@Schema(description="게시글 상태 (Y : 게시 / N : 삭제)", example="Y",
			allowableValues={"Y","N"}, defaultValue="Y")
	@Column(name="status", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status; // VARCHAR(20)
	
	@Schema(description="공지사항 작성일 (DB 자동 입력)", example="2026-08-04T10:00:00",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="created_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
	
	@Schema(description="공지사항 수정일 (DB 자동 입력)", example="2026-08-05T11:00:00",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="update_at", columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime updatedAt;
	
	@Schema(description="작성자 정보 (Employee 객체)",
			requiredMode=Schema.RequiredMode.REQUIRED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="author_id", nullable=false)
	private Employee employee;   // 작성자 식별자
	
	@Schema(description="조회수 (서버 자동 관리)", example="0",
			accessMode=Schema.AccessMode.READ_ONLY, defaultValue="0")
	@Column(name="count", columnDefinition="INT DEFAULT 0")
	private int count;	         // 공지사항 조회수 
	
}
