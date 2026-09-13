package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.domain.SafetyDocument;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

import lombok.RequiredArgsConstructor;

/** 활성 상태이며 현재 인덱싱 설정에 대한 인덱싱이 완료된 안전문서를 조회한다. */
@Service
@RequiredArgsConstructor
public class SearchableSafetyDocumentService {
    private final SafetyDocumentRepository documentRepository;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final VectorIndexManifestService manifestService;

    /** 문서 ID 오름차순으로 반환한다. */
    @Transactional(readOnly = true)
    public List<SafetyDocument> findDocuments() {
        List<SafetyDocument> activeDocuments = documentRepository
                .findByStatusOrderByIdAsc(SafetyDocumentStatus.ACTIVE);
        List<String> activeFingerprints = activeDocuments.stream()
                .map(document -> fingerprintFactory.create(document.getContentChecksum()))
                .toList();
        Set<String> completedFingerprints = manifestService
                .getCompletedFingerprints(activeFingerprints);

        return activeDocuments.stream()
                .filter(document -> completedFingerprints.contains(
                        fingerprintFactory.create(document.getContentChecksum())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<String> findFingerprints() {
        return findDocuments().stream()
                .map(document -> fingerprintFactory.create(document.getContentChecksum()))
                .sorted()
                .toList();
    }
}
