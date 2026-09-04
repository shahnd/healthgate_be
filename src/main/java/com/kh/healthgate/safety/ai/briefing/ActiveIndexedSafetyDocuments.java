package com.kh.healthgate.safety.ai.briefing;

import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.safety.ai.index.VectorIndexFingerprintFactory;
import com.kh.healthgate.safety.ai.index.VectorIndexManifestService;
import com.kh.healthgate.safety.domain.SafetyDocumentStatus;
import com.kh.healthgate.safety.repository.SafetyDocumentRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ActiveIndexedSafetyDocuments {
    private final SafetyDocumentRepository documentRepository;
    private final VectorIndexFingerprintFactory fingerprintFactory;
    private final VectorIndexManifestService manifestService;

    @Transactional(readOnly = true)
    public List<String> getFingerprints() {
        List<String> activeFingerprints = documentRepository
                .findContentChecksumsByStatus(SafetyDocumentStatus.ACTIVE)
                .stream()
                .map(checksum -> fingerprintFactory.create(checksum))
                .toList();
        Set<String> completedFingerprints = manifestService
                .getCompletedFingerprints(activeFingerprints);

        return completedFingerprints.stream()
                .sorted()
                .toList();
    }
}
