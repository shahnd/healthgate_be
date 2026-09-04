package com.kh.healthgate.safety.dto;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.media.Schema;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "안전문서 등록 요청")
public class SafetyDocumentCreateRequest {
    @Schema(description = "문서 제목", example = "물류센터 지게차 안전수칙")
    @NotBlank
    @Size(max = 200)
    private String title;

    @Schema(description = "문서 설명", example = "지게차 운행 및 작업자 안전수칙")
    @Size(max = 2000)
    private String description;

    @Schema(description = "등록할 PDF 파일", type = "string", format = "binary")
    @NotNull
    private MultipartFile file;
}
