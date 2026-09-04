package com.kh.healthgate.notice.model.vo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

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
@Table(name="notice_files")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticeFile {

	@Schema(description="공지사항첨부파일 번호 (자동 생성)", example="1",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Id
	@Column(name="notice_file_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long noticeFileId;
	
	@Schema(description="공지사항첨부파일 원본파일명", example="여름철 주의사항 안내.pdf", 
			requiredMode=Schema.RequiredMode.REQUIRED)
	@Column(name="origin_name", nullable=false, length=255)
	private String originName;      // 원본 파일명
	
	@Schema(description="공지사항첨부파일 저장된 파일명 (중복방지용 변경명)", example="2026080411234098765.jpg",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="saved_name", nullable=false, length=255)
	private String savedName;      // 저장된 파일명 
	
	@Schema(description="공지사항첨부파일 저장된 경로", example="/uploads/notices/healthgate",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="saved_path", nullable=false, length=255)
	private String savedPath;      // 저장된 경로
	
	@Schema(description="공지사항첨부파일 확장자", example=".jpg",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="extension", nullable=false, length=255)
	private String extension;      // 확장자
	
	@Schema(description="공지사항첨부파일 공지사항 식별자",
			requiredMode=Schema.RequiredMode.REQUIRED)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="notice_id",  nullable=false)
	private Notice notices;    // 공지사항 식별자
	
}
