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

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :to,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :from
            """)
    int transitionStatus(
            @Param("fingerprint") String fingerprint,
            @Param("from") VectorIndexStatus from,
            @Param("to") VectorIndexStatus to);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :indexing
            """)
    int updateHeartbeat(
            @Param("fingerprint") String fingerprint,
            @Param("indexing") VectorIndexStatus indexing);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :completed,
                   manifest.failureMessage = null,
                   manifest.chunkCount = :chunkCount,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :indexing
            """)
    int completeIndexing(
            @Param("fingerprint") String fingerprint,
            @Param("chunkCount") int chunkCount,
            @Param("indexing") VectorIndexStatus indexing,
            @Param("completed") VectorIndexStatus completed);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :failed,
                   manifest.failureMessage = :failureMessage,
                   manifest.chunkCount = null,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :indexing
            """)
    int failIndexing(
            @Param("fingerprint") String fingerprint,
            @Param("failureMessage") String failureMessage,
            @Param("indexing") VectorIndexStatus indexing,
            @Param("failed") VectorIndexStatus failed);
}
