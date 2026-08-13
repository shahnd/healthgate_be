package com.kh.healthgate.notice.model.vo;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name="NOTICEFILES")

@DynamicInsert
@DynamicUpdate

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class NoticeFiles {

	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	
	@Column(name="ORIGIN_NAME", nullable=false, length=255)
	private String originName;
	
	@Column(name="SAVED_NAME", nullable=false, length=255)
	private String savedName;
	
	@Column(name="SAVED_PATH", nullable=false, length=255)
	private String savedPath;
	
	@Column(name="EXTENSION", nullable=false, length=255)
	private String extension;
	
	// @Column(name="NOTICE_ID", nullable=false)
	// private int noticeId;
	
	@ManyToOne
	@JoinColumn(name="NOTICE_ID")
	private Notice notice;
	
}
