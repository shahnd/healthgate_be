package com.kh.healthgate.common.model.vo;

public record ApiResponse<T>(boolean success, T data, String message) {

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<T>(true, data, "요청 성공");
    }

    public static ApiResponse<Void> successWithNoData(String message) {
        return new ApiResponse<>(true, null, message);
    }

    public static ApiResponse<Void> fail(String errorMessage) {
        return new ApiResponse<Void>(false, null, errorMessage);
    }
}
