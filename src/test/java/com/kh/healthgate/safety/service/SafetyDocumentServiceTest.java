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
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import com.kh.healthgate.auth.model.vo.AuthenticatedEmployee;
import com.kh.healthgate.auth.service.AuthenticatedEmployeeService;
import com.kh.healthgate.employee.model.vo.Employee;
import com.kh.healthgate.employee.model.vo.role;
import com.kh.healthgate.file.exception.FileStorageException;
import com.kh.healthgate.file.storage.FileStorage;
import com.kh.healthgate.file.storage.StoredFile;
import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.ai.index.VectorIndexRequestedEvent;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.dto.SafetyDocumentFile;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.event.SafetyDocumentDeletedEvent;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

@ExtendWith(MockitoExtension.class)
class SafetyDocumentServiceTest {
    @Mock
    private SafetyDocumentRepository safetyDocumentRepository;

    @Mock
    private FileStorage fileStorage;

    @Mock
    private AuthenticatedEmployeeService authenticatedEmployeeService;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private VectorIndexFingerprintFactory fingerprintFactory;

    @Mock
    private VectorIndexManifestService manifestService;

    private SafetyDocumentService safetyDocumentService;

    @BeforeEach
    void setUp() {
        safetyDocumentService = new SafetyDocumentService(
                safetyDocumentRepository,
                fileStorage,
                authenticatedEmployeeService,
                eventPublisher,
                fingerprintFactory,
                manifestService);
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
        when(fingerprintFactory.create("checksum")).thenReturn("fingerprint");

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
        assertSame(SafetyDocumentStatus.ACTIVE, savedDocument.getStatus());
        assertSame(employee, savedDocument.getCreatedBy());
        assertSame(employee, savedDocument.getUpdatedBy());
        verify(manifestService).prepare("fingerprint", "checksum");
        verify(eventPublisher).publishEvent(
                new VectorIndexRequestedEvent("documents/manual.pdf", "checksum"));
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
        assertEquals(1L, result.createdById());
        assertEquals(1L, result.updatedById());
    }

    @Test
    void getsSafetyDocumentList() {
        // given
        Employee employee = employee(role.HEALTH_ADMIN);
        employee.setId(1L);
        SafetyDocument document = document(employee);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<SafetyDocument> documents = new PageImpl<>(List.of(document), pageable, 1);

        when(safetyDocumentRepository.findAll(pageable)).thenReturn(documents);

        // when
        Page<SafetyDocumentResponse> result = safetyDocumentService.getList(pageable);

        // then
        assertEquals(1, result.getTotalElements());
        assertEquals("안전수칙", result.getContent().getFirst().title());
        assertEquals(1L, result.getContent().getFirst().createdById());
    }

    @Test
    void updatesSafetyDocumentMetadata() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee creator = employee(role.HEALTH_ADMIN);
        creator.setId(1L);
        Employee updater = employee(role.HEALTH_ADMIN);
        updater.setId(2L);
        SafetyDocument document = document(creator);

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(updater);
        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));

        // when
        SafetyDocumentResponse result = safetyDocumentService.update(
                10L,
                "  변경된 안전수칙  ",
                "   ",
                loggedInEmployee);

        // then
        assertEquals("변경된 안전수칙", document.getTitle());
        assertNull(document.getDescription());
        assertSame(updater, document.getUpdatedBy());
        assertEquals("변경된 안전수칙", result.title());
        assertEquals(2L, result.updatedById());
    }

    @Test
    void rejectsMetadataUpdateWithoutHealthAdminRole() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee))
                .thenReturn(employee(role.EMPLOYEE));

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.update(10L, "안전수칙", null, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.FORBIDDEN, exception.problemType());
        verifyNoInteractions(safetyDocumentRepository);
    }

    @Test
    void deactivatesSafetyDocument() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        employee.setId(1L);
        SafetyDocument document = document(employee);

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));

        // when
        SafetyDocumentResponse result = safetyDocumentService.updateActivation(
                10L,
                false,
                loggedInEmployee);

        // then
        assertSame(SafetyDocumentStatus.INACTIVE, document.getStatus());
        assertSame(SafetyDocumentStatus.INACTIVE, result.status());
        assertSame(employee, document.getUpdatedBy());
    }

    @Test
    void activatesSafetyDocument() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        employee.setId(1L);
        SafetyDocument document = document(employee);
        document.deactivate(employee);

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));

        // when
        SafetyDocumentResponse result = safetyDocumentService.updateActivation(
                10L,
                true,
                loggedInEmployee);

        // then
        assertSame(SafetyDocumentStatus.ACTIVE, document.getStatus());
        assertSame(SafetyDocumentStatus.ACTIVE, result.status());
    }

    @Test
    void rejectsActivationUpdateWithoutHealthAdminRole() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee))
                .thenReturn(employee(role.EMPLOYEE));

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.updateActivation(10L, false, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.FORBIDDEN, exception.problemType());
        verifyNoInteractions(safetyDocumentRepository);
    }

    @Test
    void deletesSafetyDocumentAndPublishesFileCleanupEvent() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        Employee employee = employee(role.HEALTH_ADMIN);
        SafetyDocument document = document(employee);

        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee)).thenReturn(employee);
        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));

        // when
        safetyDocumentService.delete(10L, loggedInEmployee);

        // then
        verify(safetyDocumentRepository).delete(document);
        verify(eventPublisher).publishEvent(
                new SafetyDocumentDeletedEvent("documents/manual.pdf"));
        verifyNoInteractions(fileStorage);
    }

    @Test
    void rejectsDeleteWithoutHealthAdminRole() {
        // given
        AuthenticatedEmployee loggedInEmployee = loggedInEmployee();
        when(authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee))
                .thenReturn(employee(role.EMPLOYEE));

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.delete(10L, loggedInEmployee));

        // then
        assertSame(SafetyDocumentProblem.FORBIDDEN, exception.problemType());
        verifyNoInteractions(safetyDocumentRepository, eventPublisher, fileStorage);
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
    void getsSafetyDocumentFile() {
        // given
        SafetyDocument document = document(employee(role.HEALTH_ADMIN));
        Resource resource = new ByteArrayResource("file-content".getBytes());

        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(fileStorage.load("documents/manual.pdf")).thenReturn(resource);

        // when
        SafetyDocumentFile result = safetyDocumentService.getFile(10L);

        // then
        assertSame(resource, result.resource());
        assertEquals("manual.pdf", result.filename());
        assertEquals("application/pdf", result.contentType());
        assertEquals(100L, result.size());
    }

    @Test
    void convertsFileLoadFailureToDomainException() {
        // given
        SafetyDocument document = document(employee(role.HEALTH_ADMIN));
        FileStorageException cause = new FileStorageException("파일 없음");

        when(safetyDocumentRepository.findById(10L)).thenReturn(Optional.of(document));
        when(fileStorage.load("documents/manual.pdf")).thenThrow(cause);

        // when
        SafetyDocumentException exception = assertThrows(
                SafetyDocumentException.class,
                () -> safetyDocumentService.getFile(10L));

        // then
        assertSame(SafetyDocumentProblem.FILE_LOAD_FAILED, exception.problemType());
        assertSame(cause, exception.getCause());
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
