package com.kh.healthgate.hospital.model.vo;

import java.time.LocalDateTime;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Schema(description="검진 가능 병원(url 클릭시 이동)")

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
    
	@Schema(description="병원 등록시 생성 순서(자동 생성)", example="1",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Id
	@Column(name="hospital_id")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long hospitalId; 
	
	@Schema(description="병원 이름",example="서울아산병원",
			requiredMode=Schema.RequiredMode.REQUIRED)
	@Column(name="name", nullable=false, length=255)
	private String name; // VARCHAR(255)
	
	@Schema(description="병원 주소",example="서울특별시 송파구 올림픽로43길 88(풍납동 388-1)",
			requiredMode=Schema.RequiredMode.REQUIRED)
	@Column(name="address", nullable=false, length=500)
	private String address; // VARCHAR(500)
	
	@Schema(description="병원 전화 번호", example="1688-7575")
	@Column(name="phone", length=20)
	private String phone; // VARCHAR(20)
	
	@Schema(description="병원 홈페이지 url", example="https://www.amc.seoul.kr/asan/main.do")
	@Column(name="url", length=255)
	private String url; // VARCHAR(255)
	
	@Schema(description="병원 안내 문구", example="병원 셔틀버스는 당일 예약환자와 보호자만 이용 가능하오니 탑승 시 진료예약증, 진료카드, 진료예약 문자를 보여주시기 바랍니다.\r\n"
												+ "(장례식장 이용, 일반용무 등으로오신 분은 이용 불가)\r\n"
												+ "토요일, 일요일, 공휴일은 운행하지 않습니다.")
	@Column(name="description", columnDefinition="TEXT")
	private String description; // TEXT
	
	@Schema(description="일반검진", example="true")
	@Column(name="is_general_exam_available", nullable=false )
	private boolean isGeneralExamAvailable;
	
	@Schema(description="위암검진", example="true")
	@Column(name="is_stomach_cancer_exam_available", nullable=false)
	private boolean isStomachCancerExamAvailable;
	

	@Schema(description="대장암검진", example="true")
	@Column(name="is_colon_cancer_exam_available", nullable=false)
	private boolean isColonCancerExamAvailable;
	
	@Schema(description="간암검진", example="true")
	@Column(name="is_liver_cancer_exam_available", nullable=false)
	private boolean isLiverCancerExamAvailable;
	
	@Schema(description="폐암검진", example="true")
	@Column(name="is_lung_cancer_exam_available", nullable=false)
	private boolean isLungCancerExamAvailable;
	
	@Schema(description="작성일 (DB 자동 입력)", example="2026-08-04T10:00:00",
			accessMode=Schema.AccessMode.READ_ONLY)
	@Column(name="created_at", nullable=false, columnDefinition="TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private LocalDateTime createdAt;
	
	@Schema(description="병원 게시 상태 (Y : 게시 / N : 삭제)", example="Y",
			allowableValues={"Y","N"}, defaultValue="Y")
	@Column(name="status", columnDefinition="CHAR(1) DEFAULT 'Y'")
	private String status; 
	
	@PrePersist
	public void prePersist() {
	    if (this.createdAt == null) {
	        this.createdAt = LocalDateTime.now();
	    }
	    if (this.status == null) {
	        this.status = "Y";
	    }
	}

}
