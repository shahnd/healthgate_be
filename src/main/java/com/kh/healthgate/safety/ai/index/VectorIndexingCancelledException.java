package com.kh.healthgate.safety.ai.index;

class VectorIndexingCancelledException extends RuntimeException {
    VectorIndexingCancelledException() {
        super("사용자 요청으로 인덱싱이 중단되었습니다.");
    }
}
