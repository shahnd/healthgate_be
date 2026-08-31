package com.kh.healthgate.safety.service;

import java.io.IOException;

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
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.exception.SafetyDocumentException;
import com.kh.healthgate.safety.exception.SafetyDocumentProblem;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SafetyDocumentService {
    private static final long MAX_FILE_SIZE = 20L * 1024 * 1024;

    private final SafetyDocumentRepository safetyDocumentRepository;
    private final FileStorage fileStorage;
    private final AuthenticatedEmployeeService authenticatedEmployeeService;

    @Transactional
    public SafetyDocument create(
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
        SafetyDocument document = new SafetyDocument(
                title.strip(),
                normalizeDescription(description),
                storedFile.originalFilename(),
                storedFile.storageKey(),
                storedFile.contentType(),
                storedFile.size(),
                storedFile.checksum(),
                employee);

        return safetyDocumentRepository.save(document);
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
}
