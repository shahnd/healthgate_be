package com.kh.healthgate.safety.ai.index;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VectorIndexManifestRepository extends JpaRepository<VectorIndexManifest, String> {
}
