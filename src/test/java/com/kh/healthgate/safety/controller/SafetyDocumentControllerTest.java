package com.kh.healthgate.safety.controller;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.auth.web.AuthenticatedEmployeeArgumentResolver;
import com.kh.healthgate.common.exception.ApiExceptionHandler;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.dto.SafetyDocumentFile;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
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
                .setCustomArgumentResolvers(
                        new AuthenticatedEmployeeArgumentResolver(),
                        new PageableHandlerMethodArgumentResolver())
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
                .andExpect(jsonPath("$.indexStatus").hasJsonPath())
                .andExpect(jsonPath("$.createdById").value(1));
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
                .andExpect(jsonPath("$.createdById").value(1));
    }

    @Test
    void getsSafetyDocumentList() throws Exception {
        // given
        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        when(safetyDocumentService.getList(pageable))
                .thenReturn(new PageImpl<>(List.of(response()), pageable, 1));

        // when, then
        mockMvc.perform(get("/safety-documents"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].createdById").value(1))
                .andExpect(jsonPath("$.page.size").value(10))
                .andExpect(jsonPath("$.page.number").value(0))
                .andExpect(jsonPath("$.page.totalElements").value(1))
                .andExpect(jsonPath("$.page.totalPages").value(1));
    }

    @Test
    void updatesSafetyDocumentMetadata() throws Exception {
        // given
        when(safetyDocumentService.update(
                eq(10L),
                eq("변경된 안전수칙"),
                eq("변경된 설명"),
                any(AuthenticatedEmployee.class)))
                .thenReturn(response());

        // when, then
        mockMvc.perform(patch("/safety-documents/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "title": "변경된 안전수칙",
                          "description": "변경된 설명"
                        }
                        """)
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void rejectsBlankTitleWhenUpdatingSafetyDocument() throws Exception {
        // given
        String request = """
                {
                  "title": " ",
                  "description": "변경된 설명"
                }
                """;

        // when, then
        mockMvc.perform(patch("/safety-documents/10")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(safetyDocumentService);
    }

    @Test
    void updatesSafetyDocumentActivation() throws Exception {
        // given
        when(safetyDocumentService.updateActivation(
                eq(10L),
                eq(false),
                any(AuthenticatedEmployee.class)))
                .thenReturn(response(SafetyDocumentStatus.INACTIVE));

        // when, then
        mockMvc.perform(patch("/safety-documents/10/activation")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "active": false
                        }
                        """)
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("INACTIVE"));
    }

    @Test
    void requestsSafetyDocumentIndexing() throws Exception {
        // given
        AuthenticatedEmployee loggedInEmployee = new AuthenticatedEmployee(
                1L,
                "admin01",
                "HEALTH_ADMIN");
        when(safetyDocumentService.requestIndexing(10L, loggedInEmployee))
                .thenReturn(response());

        // when, then
        mockMvc.perform(post("/safety-documents/10/index")
                .requestAttr("empId", loggedInEmployee.id())
                .requestAttr("employeeNumber", loggedInEmployee.employeeNumber())
                .requestAttr("empRole", loggedInEmployee.role()))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.id").value(10));
    }

    @Test
    void returnsConflictWhenIndexingRequestIsDuplicated() throws Exception {
        // given
        AuthenticatedEmployee loggedInEmployee = new AuthenticatedEmployee(
                1L,
                "admin01",
                "HEALTH_ADMIN");
        when(safetyDocumentService.requestIndexing(10L, loggedInEmployee))
                .thenThrow(new SafetyDocumentException(
                        SafetyDocumentProblem.INDEXING_REQUEST_CONFLICT));

        // when, then
        mockMvc.perform(post("/safety-documents/10/index")
                .requestAttr("empId", loggedInEmployee.id())
                .requestAttr("employeeNumber", loggedInEmployee.employeeNumber())
                .requestAttr("empRole", loggedInEmployee.role()))
                .andExpect(status().isConflict())
                .andExpect(content().contentTypeCompatibleWith("application/problem+json"))
                .andExpect(jsonPath("$.code").value("VECTOR_INDEX_REQUEST_CONFLICT"));
    }

    @Test
    void rejectsMissingActivationValue() throws Exception {
        // given
        String request = "{}";

        // when, then
        mockMvc.perform(patch("/safety-documents/10/activation")
                .contentType(MediaType.APPLICATION_JSON)
                .content(request)
                .requestAttr("empId", 1L)
                .requestAttr("employeeNumber", "admin01")
                .requestAttr("empRole", "HEALTH_ADMIN"))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(safetyDocumentService);
    }

    @Test
    void deletesSafetyDocument() throws Exception {
        // given
        AuthenticatedEmployee loggedInEmployee = new AuthenticatedEmployee(
                1L,
                "admin01",
                "HEALTH_ADMIN");

        // when, then
        mockMvc.perform(delete("/safety-documents/10")
                .requestAttr("empId", loggedInEmployee.id())
                .requestAttr("employeeNumber", loggedInEmployee.employeeNumber())
                .requestAttr("empRole", loggedInEmployee.role()))
                .andExpect(status().isNoContent())
                .andExpect(content().string(""));

        verify(safetyDocumentService).delete(10L, loggedInEmployee);
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

    @Test
    void showsSafetyDocumentFileInlineByDefault() throws Exception {
        // given
        when(safetyDocumentService.getFile(10L)).thenReturn(fileResponse());

        // when, then
        mockMvc.perform(get("/safety-documents/10/file"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes("file-content".getBytes()))
                .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, "12"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        allOf(startsWith("inline;"), containsString("filename*=UTF-8''manual.pdf"))));
    }

    @Test
    void downloadsSafetyDocumentFileWhenRequested() throws Exception {
        // given
        when(safetyDocumentService.getFile(10L)).thenReturn(fileResponse());

        // when, then
        mockMvc.perform(get("/safety-documents/10/file").param("download", "true"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/pdf"))
                .andExpect(content().bytes("file-content".getBytes()))
                .andExpect(header().string(HttpHeaders.CONTENT_LENGTH, "12"))
                .andExpect(header().string(
                        HttpHeaders.CONTENT_DISPOSITION,
                        allOf(startsWith("attachment;"), containsString("filename*=UTF-8''manual.pdf"))));
    }

    private SafetyDocumentResponse response() {
        return response(SafetyDocumentStatus.ACTIVE);
    }

    private SafetyDocumentResponse response(SafetyDocumentStatus status) {
        return new SafetyDocumentResponse(
                10L,
                "안전수칙",
                "설명",
                "manual.pdf",
                "application/pdf",
                12L,
                status,
                null,
                1L,
                1L,
                LocalDateTime.of(2026, 9, 1, 10, 0),
                LocalDateTime.of(2026, 9, 1, 10, 0));
    }

    private SafetyDocumentFile fileResponse() {
        return new SafetyDocumentFile(
                new ByteArrayResource("file-content".getBytes()),
                "manual.pdf",
                "application/pdf",
                12L);
    }
}
