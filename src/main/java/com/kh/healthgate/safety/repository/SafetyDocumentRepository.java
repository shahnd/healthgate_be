package com.kh.healthgate.safety.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.safety.domain.SafetyDocument;

public interface SafetyDocumentRepository extends JpaRepository<SafetyDocument, Long> {
}
