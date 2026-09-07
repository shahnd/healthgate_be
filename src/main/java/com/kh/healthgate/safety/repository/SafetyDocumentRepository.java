package com.kh.healthgate.safety.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;

public interface SafetyDocumentRepository extends JpaRepository<SafetyDocument, Long> {
    boolean existsByContentChecksum(String contentChecksum);

    @Query("select document.contentChecksum from SafetyDocument document where document.status = :status")
    List<String> findContentChecksumsByStatus(@Param("status") SafetyDocumentStatus status);
}
