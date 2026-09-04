package com.kh.healthgate.safety.ai.index;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VectorIndexManifestService {
    private final VectorIndexManifestRepository repository;

    @Transactional
    public VectorIndexStatus prepare(String fingerprint, String contentChecksum) {
        VectorIndexManifest manifest = repository.findById(fingerprint)
                .orElseGet(() -> repository.save(
                        new VectorIndexManifest(fingerprint, contentChecksum)));
        return manifest.getStatus();
    }

    @Transactional(readOnly = true)
    public Optional<VectorIndexStatus> getStatus(String fingerprint) {
        return repository.findById(fingerprint)
                .map(manifest -> manifest.getStatus());
    }

    @Transactional(readOnly = true)
    public Map<String, VectorIndexStatus> getStatuses(Collection<String> fingerprints) {
        return repository.findAllById(fingerprints).stream()
                .collect(Collectors.toMap(
                        manifest -> manifest.getFingerprint(),
                        manifest -> manifest.getStatus()));
    }

    @Transactional(readOnly = true)
    public boolean isCompleted(String fingerprint) {
        return repository.findById(fingerprint)
                .filter(manifest -> manifest.isCompleted())
                .isPresent();
    }

    @Transactional(readOnly = true)
    public Set<String> getCompletedFingerprints(Collection<String> fingerprints) {
        return repository.findAllById(fingerprints).stream()
                .filter(manifest -> manifest.isCompleted())
                .map(manifest -> manifest.getFingerprint())
                .collect(Collectors.toSet());
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
