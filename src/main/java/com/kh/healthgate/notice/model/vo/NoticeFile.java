package com.kh.healthgate.notice.model.vo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name="notice_file")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticeFile {

	@Id
	@Column(name="notice_file_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long noticeFileId;
	
	@Column(name="origin_name", nullable=false, length=255)
	private String originName;      // 원본 파일명
	
	@Column(name="saved_name", nullable=false, length=255)
	private String savedName;      // 저장된 파일명 
	
	@Column(name="saved_path", nullable=false, length=255)
	private String savedPath;      // 저장된 경로
	
	@Column(name="extension", nullable=false, length=255)
	private String extension;      // 확장자
	
	// @Column(name="notice_id", nullable=false)
	// private int noticeId;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="notice_id",  nullable=false)
	private Notice notice;    // 공지사항 식별자
	
}
