package com.kh.healthgate.safety.service;

import java.io.IOException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
import com.kh.healthgate.safety.dto.SafetyDocumentFile;
import com.kh.healthgate.safety.dto.SafetyDocumentResponse;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.event.SafetyDocumentDeletedEvent;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class SafetyDocumentService {
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final SafetyDocumentRepository safetyDocumentRepository;
    private final FileStorage fileStorage;
    private final AuthenticatedEmployeeService authenticatedEmployeeService;
    private final ApplicationEventPublisher eventPublisher;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final VectorIndexManifestService manifestService;

    @Transactional
    public SafetyDocumentResponse create(
            String title,
            String description,
            MultipartFile file,
            AuthenticatedEmployee loggedInEmployee) {

        Employee employee = authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee);

        if (employee.getRole() != role.HEALTH_ADMIN) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FORBIDDEN);
        }

        validateFile(file);
        StoredFile storedFile = storeFile(file);

        try {
            if (safetyDocumentRepository.existsByContentChecksum(storedFile.checksum())) {
                throw new SafetyDocumentException(SafetyDocumentProblem.DUPLICATE_FILE);
            }

            SafetyDocument document = new SafetyDocument(
                    title.strip(),
                    normalizeDescription(description),
                    storedFile.originalFilename(),
                    storedFile.storageKey(),
                    storedFile.contentType(),
                    storedFile.size(),
                    storedFile.checksum(),
                    employee);

            SafetyDocument savedDocument = safetyDocumentRepository.saveAndFlush(document);
            String fingerprint = fingerprintFactory.create(storedFile.checksum());
            manifestService.prepare(fingerprint, storedFile.checksum());
            eventPublisher.publishEvent(new VectorIndexRequestedEvent(
                    storedFile.storageKey(),
                    storedFile.checksum()));
            return SafetyDocumentResponse.from(savedDocument);
        } catch (RuntimeException exception) {
            deleteStoredFile(storedFile.storageKey(), exception);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public SafetyDocumentResponse get(Long id) {
        SafetyDocument document = getDocument(id);
        return SafetyDocumentResponse.from(document);
    }

    @Transactional(readOnly = true)
    public Page<SafetyDocumentResponse> getList(Pageable pageable) {
        return safetyDocumentRepository.findAll(pageable)
                .map(SafetyDocumentResponse::from);
    }

    @Transactional
    public SafetyDocumentResponse update(
            Long id,
            String title,
            String description,
            AuthenticatedEmployee loggedInEmployee) {
        Employee employee = authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee);

        if (employee.getRole() != role.HEALTH_ADMIN) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FORBIDDEN);
        }

        SafetyDocument document = getDocument(id);
        document.updateMetadata(title.strip(), normalizeDescription(description), employee);
        return SafetyDocumentResponse.from(document);
    }

    @Transactional
    public SafetyDocumentResponse updateActivation(
            Long id,
            boolean active,
            AuthenticatedEmployee loggedInEmployee) {
        Employee employee = authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee);

        if (employee.getRole() != role.HEALTH_ADMIN) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FORBIDDEN);
        }

        SafetyDocument document = getDocument(id);
        if (active) {
            document.activate(employee);
        } else {
            document.deactivate(employee);
        }
        return SafetyDocumentResponse.from(document);
    }

    @Transactional
    public void delete(Long id, AuthenticatedEmployee loggedInEmployee) {
        Employee employee = authenticatedEmployeeService.getLoggedInEmployee(loggedInEmployee);

        if (employee.getRole() != role.HEALTH_ADMIN) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FORBIDDEN);
        }

        SafetyDocument document = getDocument(id);
        safetyDocumentRepository.delete(document);
        eventPublisher.publishEvent(new SafetyDocumentDeletedEvent(document.getStorageKey()));
    }

    @Transactional(readOnly = true)
    public SafetyDocumentFile getFile(Long id) {
        SafetyDocument document = getDocument(id);
        try {
            return new SafetyDocumentFile(
                    fileStorage.load(document.getStorageKey()),
                    document.getOriginalFilename(),
                    document.getContentType(),
                    document.getFileSize());
        } catch (FileStorageException exception) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FILE_LOAD_FAILED, exception);
        }
    }

    private SafetyDocument getDocument(Long id) {
        return safetyDocumentRepository.findById(id)
                .orElseThrow(() -> new SafetyDocumentException(SafetyDocumentProblem.NOT_FOUND));
    }

    private void validateFile(MultipartFile file) {
        if (file == null
                || file.isEmpty()
                || file.getSize() > MAX_FILE_SIZE
                || !StringUtils.hasText(file.getOriginalFilename())) {
            throw new SafetyDocumentException(SafetyDocumentProblem.INVALID_FILE);
        }
    }

    private StoredFile storeFile(MultipartFile file) {
        String contentType = StringUtils.hasText(file.getContentType())
                ? file.getContentType()
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        try {
            return fileStorage.store(
                    file.getOriginalFilename(),
                    contentType,
                    file.getInputStream());
        } catch (IOException | FileStorageException exception) {
            throw new SafetyDocumentException(SafetyDocumentProblem.STORAGE_FAILED, exception);
        }
    }

    private String normalizeDescription(String description) {
        return StringUtils.hasText(description) ? description.strip() : null;
    }

    private void deleteStoredFile(String storageKey, RuntimeException originalException) {
        try {
            fileStorage.delete(storageKey);
        } catch (RuntimeException deleteException) {
            log.error("파일 삭제에 실패했습니다. storageKey={}", storageKey, deleteException);
            originalException.addSuppressed(deleteException);
        }
    }
}
