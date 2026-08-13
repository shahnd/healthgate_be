package com.kh.healthgate.consultation.model.vo;

public enum ConsultationStatus {
	RESERVED, // 예약/상담대기
	FINISHED, // 상담 완료(정상종료)
	CANSELED, // 예약 취소
	EXPIRED   // 상담 취소(무산)
}
