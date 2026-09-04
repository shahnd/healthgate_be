package com.kh.healthgate.safety.ai.index;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VectorIndexManifestRepository extends JpaRepository<VectorIndexManifest, String> {

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :pending,
                   manifest.failureMessage = null,
                   manifest.chunkCount = null,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :failed
            """)
    int retryFailed(
            @Param("fingerprint") String fingerprint,
            @Param("failed") VectorIndexStatus failed,
            @Param("pending") VectorIndexStatus pending);
}
