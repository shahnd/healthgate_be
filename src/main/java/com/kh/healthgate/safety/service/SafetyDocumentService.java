package com.kh.healthgate.safety.service;

import java.io.IOException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.Resource;
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
import com.kh.healthgate.safety.ai.index.VectorIndexStatus;
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

        if (file == null) {
            throw new SafetyDocumentException(SafetyDocumentProblem.INVALID_FILE);
        }

        return create(
                title,
                description,
                file.getResource(),
                file.getOriginalFilename(),
                file.getContentType(),
                file.getSize(),
                employee);
    }

    @Transactional
    public SafetyDocumentResponse create(
            String title,
            String description,
            Resource resource,
            String filename,
            String contentType,
            long fileSize,
            Employee employee) {

        if (employee.getRole() != role.HEALTH_ADMIN) {
            throw new SafetyDocumentException(SafetyDocumentProblem.FORBIDDEN);
        }

        validateFile(resource, filename, fileSize);
        StoredFile storedFile = storeFile(resource, filename, contentType);

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
            VectorIndexStatus indexStatus = manifestService.prepare(
                    fingerprint,
                    storedFile.checksum());
            eventPublisher.publishEvent(new VectorIndexRequestedEvent(
                    storedFile.storageKey(),
                    storedFile.checksum()));
            return SafetyDocumentResponse.from(savedDocument, indexStatus);
        } catch (RuntimeException exception) {
            deleteStoredFile(storedFile.storageKey(), exception);
            throw exception;
        }
    }

    @Transactional(readOnly = true)
    public SafetyDocumentResponse get(Long id) {
        SafetyDocument document = getDocument(id);
        return toResponse(document);
    }

    @Transactional(readOnly = true)
    public Page<SafetyDocumentResponse> getList(Pageable pageable) {
        Page<SafetyDocument> documents = safetyDocumentRepository.findAll(pageable);
        Map<String, String> fingerprintByChecksum = documents.stream()
                .map(document -> document.getContentChecksum())
                .distinct()
                .collect(Collectors.toMap(
                        Function.identity(),
                        fingerprintFactory::create));
        Map<String, VectorIndexStatus> statusByFingerprint = manifestService.getStatuses(
                fingerprintByChecksum.values());

        return documents.map(document -> SafetyDocumentResponse.from(
                document,
                statusByFingerprint.get(
                        fingerprintByChecksum.get(document.getContentChecksum()))));
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
        return toResponse(document);
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
        return toResponse(document);
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

    private SafetyDocumentResponse toResponse(SafetyDocument document) {
        String fingerprint = fingerprintFactory.create(document.getContentChecksum());
        VectorIndexStatus indexStatus = manifestService.getStatus(fingerprint).orElse(null);
        return SafetyDocumentResponse.from(document, indexStatus);
    }

    private void validateFile(Resource resource, String filename, long fileSize) {
        if (resource == null
                || fileSize <= 0
                || fileSize > MAX_FILE_SIZE
                || !StringUtils.hasText(filename)) {
            throw new SafetyDocumentException(SafetyDocumentProblem.INVALID_FILE);
        }
    }

    private StoredFile storeFile(Resource resource, String filename, String contentType) {
        String resolvedContentType = StringUtils.hasText(contentType)
                ? contentType
                : MediaType.APPLICATION_OCTET_STREAM_VALUE;
        try {
            return fileStorage.store(
                    filename,
                    resolvedContentType,
                    resource.getInputStream());
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
