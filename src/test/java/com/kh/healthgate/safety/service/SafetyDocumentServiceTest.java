package com.kh.healthgate.safety.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.InputStream;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.auth.service.AuthenticatedEmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.role;
import com.kh.healthgate.file.exception.FileStorageException;
import com.kh.healthgate.file.storage.FileStorage;
import com.kh.healthgate.file.storage.StoredFile;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

@ExtendWith(MockitoExtension.class)
class SafetyDocumentServiceTest {
    @Mock
    private SafetyDocumentRepository safetyDocumentRepository;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private AuthenticatedEmployeeService authenticatedEmployeeService;

    private SafetyDocumentService safetyDocumentService;

    @BeforeEach
    void setUp() {
        safetyDocumentService = new SafetyDocumentService(
                safetyDocumentRepository,
                fileStorage,
                authenticatedEmployeeService);
    }

    @Test
    void createsSafetyDocument() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        MockMultipartFile file = file();
        StoredFile storedFile = new StoredFile(
                "documents/manual.pdf",
                "manual.pdf",
                "application/pdf",
                file.getSize(),
                "checksum");

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(fileStorage.store(eq("manual.pdf"), eq("application/pdf"), any(InputStream.class)))
                .thenReturn(storedFile);
        when(safetyDocumentRepository.saveAndFlush(any(SafetyDocument.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // when
        SafetyDocumentResponse result = safetyDocumentService.create(
                "  안전수칙  ",
                "   ",
                file,
                loggedInEmployee);

        // then
        ArgumentCaptor<SafetyDocument> documentCaptor = ArgumentCaptor.forClass(SafetyDocument.class);
        verify(safetyDocumentRepository).saveAndFlush(documentCaptor.capture());
        SafetyDocument savedDocument = documentCaptor.getValue();

        assertEquals("안전수칙", result.title());
        assertNull(result.description());
        assertEquals("안전수칙", savedDocument.getTitle());
        assertNull(savedDocument.getDescription());
        assertEquals("manual.pdf", savedDocument.getOriginalFilename());
        assertEquals("documents/manual.pdf", savedDocument.getStorageKey());
        assertEquals("checksum", savedDocument.getContentChecksum());
        assertSame(employee, savedDocument.getCreatedBy());
        assertSame(employee, savedDocument.getUpdatedBy());
    }

    @Test
    void getsSafetyDocument() {
        // given
        Employee employee = employee(role.HEALTH_ADMIN);
        employee.setId(1L);
        employee.setEmployeeNumber("admin01");
        employee.setName("관리자");
        SafetyDocument document = document(employee);

        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));

        // when
        SafetyDocumentResponse result = safetyDocumentService.get(10L);

        // then
        assertEquals("안전수칙", result.title());
        assertEquals("설명", result.description());
        assertEquals("manual.pdf", result.originalFilename());
        assertEquals("application/pdf", result.contentType());
        assertEquals(100L, result.fileSize());
        assertEquals("admin01", result.createdBy().employeeNumber());
        assertEquals("admin01", result.updatedBy().employeeNumber());
    }

    @Test
    void throwsNotFoundWhenSafetyDocumentDoesNotExist() {
        // given
        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.empty());

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.get(10L));

        // then
        assertSame(SafetyDocumentProblem.NOT_FOUND, exception.problemType());
    }

    @Test
    void rejectsEmployeeWithoutHealthAdminRole() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee))
                .thenReturn(employee(role.EMPLOYEE));

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.create("안전수칙", null, file(), loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.FORBIDDEN, exception.problemType());
        verifyNoInteractions(fileStorage, safetyDocumentRepository);
    }

    @Test
    void rejectsEmptyFile() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee))
                .thenReturn(employee(role.HEALTH_ADMIN));
        MockMultipartFile emptyFile = new MockMultipartFile(
                "file", "manual.pdf", "application/pdf", new byte[0]);

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.create("안전수칙", null, emptyFile, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.INVALID_FILE, exception.problemType());
        verifyNoInteractions(fileStorage, safetyDocumentRepository);
    }

    @Test
    void convertsFileStorageFailureToDomainException() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        MockMultipartFile file = file();
        FileStorageException cause = new FileStorageException("저장 실패");

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(fileStorage.store(eq("manual.pdf"), eq("application/pdf"), any(InputStream.class)))
                .thenThrow(cause);

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.create("안전수칙", null, file, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.STORAGE_FAILED, exception.problemType());
        assertSame(cause, exception.getCause());
        verifyNoInteractions(safetyDocumentRepository);
    }

    @Test
    void deletesStoredFileWhenContentIsDuplicated() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        MockMultipartFile file = file();
        StoredFile storedFile = storedFile(file);

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(fileStorage.store(eq("manual.pdf"), eq("application/pdf"), any(InputStream.class)))
                .thenReturn(storedFile);
        when(safetyDocumentRepository.existsByContentChecksum("checksum")).thenReturn(true);

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.create("안전수칙", null, file, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.DUPLICATE_FILE, exception.problemType());
        verify(fileStorage).delete("documents/manual.pdf");
    }

    @Test
    void deletesStoredFileWhenDatabaseSaveFails() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        MockMultipartFile file = file();
        StoredFile storedFile = storedFile(file);
        IllegalStateException databaseException = new IllegalStateException("DB 저장 실패");

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(fileStorage.store(eq("manual.pdf"), eq("application/pdf"), any(InputStream.class)))
                .thenReturn(storedFile);
        when(safetyDocumentRepository.saveAndFlush(any(SafetyDocument.class)))
                .thenThrow(databaseException);

        // when
        IllegalStateException exception = assertThrows(
                IllegalStateException.class,
                () -> safetyDocumentService.create("안전수칙", null, file, loggedInEmployee));

        // then
        assertSame(databaseException, exception);
        verify(fileStorage).delete("documents/manual.pdf");
    }

    @Test
    void keepsOriginalExceptionWhenCompensationDeleteFails() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        MockMultipartFile file = file();
        StoredFile storedFile = storedFile(file);
        FileStorageException deleteException = new FileStorageException("삭제 실패");

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(fileStorage.store(eq("manual.pdf"), eq("application/pdf"), any(InputStream.class)))
                .thenReturn(storedFile);
        when(safetyDocumentRepository.existsByContentChecksum("checksum")).thenReturn(true);
        doThrow(deleteException).when(fileStorage).delete("documents/manual.pdf");

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.create("안전수칙", null, file, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.DUPLICATE_FILE, exception.problemType());
        assertSame(deleteException, exception.getSuppressed()[0]);
    }

    private AuthenticatedEmployee loggedInEmployee() {
        return new AuthenticatedEmployee(1L, "admin01", "HEALTH_ADMIN");
    }

    private Employee employee(role employeeRole) {
        Employee employee = new Employee();
        employee.setRole(employeeRole);
        return employee;
    }

    private MockMultipartFile file() {
        return new MockMultipartFile(
                "file",
                "manual.pdf",
                "application/pdf",
                "file-content".getBytes());
    }

    private StoredFile storedFile(MockMultipartFile file) {
        return new StoredFile(
                "documents/manual.pdf",
                "manual.pdf",
                "application/pdf",
                file.getSize(),
                "checksum");
    }

    private SafetyDocument document(Employee employee) {
        return new SafetyDocument(
                "안전수칙",
                "설명",
                "manual.pdf",
                "documents/manual.pdf",
                "application/pdf",
                100L,
                "checksum",
                employee);
    }
}
