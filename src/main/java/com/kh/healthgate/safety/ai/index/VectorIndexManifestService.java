package com.kh.healthgate.safety.ai.index;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VectorIndexManifestService {
    private final VectorIndexManifestRepository repository;

    @Transactional
    public void prepare(String fingerprint, String contentChecksum) {
        if (!repository.existsById(fingerprint)) {
            repository.save(new VectorIndexManifest(fingerprint, contentChecksum));
        }
    }

    @Transactional(readOnly = true)
    public boolean isCompleted(String fingerprint) {
        return repository.findById(fingerprint)
                .filter(manifest -> manifest.isCompleted())
                .isPresent();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void startIndexing(String fingerprint, String contentChecksum) {
        VectorIndexManifest manifest = repository.findById(fingerprint).orElse(null);
        if (manifest == null) {
            manifest = repository.save(new VectorIndexManifest(fingerprint, contentChecksum));
        }
        manifest.start();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void completeIndexing(String fingerprint, int chunkCount) {
        repository.findById(fingerprint).orElseThrow().complete(chunkCount);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void failIndexing(String fingerprint, String failureMessage) {
        repository.findById(fingerprint).orElseThrow().fail(failureMessage);
    }
}
