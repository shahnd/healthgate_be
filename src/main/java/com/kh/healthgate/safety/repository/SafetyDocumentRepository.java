package com.kh.healthgate.safety.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;

public interface SafetyDocumentRepository extends JpaRepository<SafetyDocument, Long> {
    boolean existsByContentChecksum(String contentChecksum);

    List<SafetyDocument> findByStatusOrderByIdAsc(SafetyDocumentStatus status);
}
