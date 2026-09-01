package com.kh.healthgate.safety.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.auth.web.AuthenticatedEmployeeArgumentResolver;
import com.kh.healthgate.common.exception.ApiExceptionHandler;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.service.SafetyDocumentService;

@ExtendWith(MockitoExtension.class)
class SafetyDocumentControllerTest {
    @Mock
    private SafetyDocumentService safetyDocumentService;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(new SafetyDocumentController(safetyDocumentService))
                .setCustomArgumentResolvers(new AuthenticatedEmployeeArgumentResolver())
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    @Test
    void createsSafetyDocument() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "manual.pdf", "application/pdf", "file-content".getBytes());
        SafetyDocumentResponse response = response();

        when(safetyDocumentService.create(
                eq("안전수칙"), eq("설명"), any(MultipartFile.class), any(AuthenticatedEmployee.class)))
                .thenReturn(response);

        // when, then
        mockMvc.perform(multipart("/safety-documents")
                .file(file)
                .param("title", "안전수칙")
                .param("description", "설명")
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/safety-documents/10"))
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("안전수칙"))
                .andExpect(jsonPath("$.originalFilename").value("manual.pdf"))
                .andExpect(jsonPath("$.createdBy.employeeNumber").value("admin01"));
    }

    @Test
    void rejectsBlankTitle() throws Exception {
        // given
        MockMultipartFile file = new MockMultipartFile(
                "file", "manual.pdf", "application/pdf", "file-content".getBytes());

        // when, then
        mockMvc.perform(multipart("/safety-documents")
                .file(file)
                .param("title", " ")
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(safetyDocumentService);
    }

    @Test
    void getsSafetyDocument() throws Exception {
        // given
        when(safetyDocumentService.get(10L)).thenReturn(response());

        // when, then
        mockMvc.perform(get("/safety-documents/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.title").value("안전수칙"))
                .andExpect(jsonPath("$.createdBy.employeeNumber").value("admin01"));
    }

    @Test
    void returnsNotFoundWhenSafetyDocumentDoesNotExist() throws Exception {
        // given
        when(safetyDocumentService.get(10L))
                .thenThrow(new SafetyDocumentException(SafetyDocumentProblem.NOT_FOUND));

        // when, then
        mockMvc.perform(get("/safety-documents/10"))
                .andExpect(status().isNotFound())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.code").value("SAFETY_DOCUMENT_NOT_FOUND"));
    }

    private SafetyDocumentResponse response() {
        SafetyDocumentResponse.EmployeeResponse employee = new SafetyDocumentResponse.EmployeeResponse(1L, "admin01",
                "관리자");
        return new SafetyDocumentResponse(
                10L,
                "안전수칙",
                "설명",
                "manual.pdf",
                "application/pdf",
                12L,
                employee,
                employee,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 10, 0));
    }
}
