package com.kh.healthgate.safety.exception;

public class VectorIndexingCancelledException extends RuntimeException {
    public VectorIndexingCancelledException() {
        super("사용자 요청으로 인덱싱이 중단되었습니다.");
    }
}
