package com.kh.healthgate.safety.ai.model.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.kh.healthgate.safety.ai.model.dao.VectorIndexManifestRepository;
import com.kh.healthgate.safety.ai.model.vo.VectorIndexManifest;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VectorIndexManifestService {
    private final VectorIndexManifestRepository repository;

    @Transactional(readOnly = true)
    public boolean isCompleted(String fingerprint) {
        return repository.findById(fingerprint)
                .filter(manifest -> manifest.isCompleted())
                .isPresent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startIndexing(String fingerprint, String sourceName) {
        VectorIndexManifest manifest = repository.findById(fingerprint).orElse(null);
        if (manifest == null) {
            repository.save(new VectorIndexManifest(fingerprint, sourceName));
            return;
        }
        manifest.restart(sourceName);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeIndexing(String fingerprint) {
        repository.findById(fingerprint).orElseThrow().complete();
    }
}
