package com.kh.healthgate.safety.ai.index;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.EnumSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VectorIndexManifestRepository extends JpaRepository<VectorIndexManifest, String> {

    default boolean retryIndexing(String fingerprint) {
        return retryIndexing(
                fingerprint,
                EnumSet.of(VectorIndexStatus.FAILED, VectorIndexStatus.CANCELLED),
                VectorIndexStatus.PENDING) == 1;
    }

    default boolean transitionStatus(String fingerprint, VectorIndexStatus from, VectorIndexStatus to) {
        return transitionStatus(fingerprint, EnumSet.of(from), to) == 1;
    }

    default boolean startIndexing(String fingerprint) {
        return transitionStatus(fingerprint, VectorIndexStatus.PENDING, VectorIndexStatus.INDEXING);
    }

    default boolean cancelPendingIndexing(String fingerprint) {
        return transitionStatus(fingerprint, VectorIndexStatus.PENDING, VectorIndexStatus.CANCELLED);
    }

    default boolean requestCancellation(String fingerprint) {
        return transitionStatus(fingerprint, VectorIndexStatus.INDEXING, VectorIndexStatus.CANCEL_REQUESTED);
    }

    default boolean completeCancellation(String fingerprint) {
        return transitionStatus(fingerprint, VectorIndexStatus.CANCEL_REQUESTED, VectorIndexStatus.CANCELLED);
    }

    default boolean completeIndexing(String fingerprint, int chunkCount) {
        return completeIndexing(
                fingerprint, chunkCount, VectorIndexStatus.INDEXING, VectorIndexStatus.COMPLETED) == 1;
    }

    default boolean failIndexing(String fingerprint, String failureMessage) {
        return failIndexing(
                fingerprint, failureMessage, VectorIndexStatus.INDEXING, VectorIndexStatus.FAILED) == 1;
    }

    default boolean failHangingIndexing(String fingerprint, String failureMessage, LocalDateTime deadline) {
        return failHangingIndexing(
                fingerprint, failureMessage, deadline, VectorIndexStatus.INDEXING, VectorIndexStatus.FAILED) == 1;
    }

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :pending,
                   manifest.failureMessage = null,
                   manifest.chunkCount = null,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status in :from
            """)
    int retryIndexing(
            @Param("fingerprint") String fingerprint,
            @Param("from") Collection<VectorIndexStatus> from,
            @Param("pending") VectorIndexStatus pending);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :to,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status in :from
            """)
    int transitionStatus(
            @Param("fingerprint") String fingerprint,
            @Param("from") Collection<VectorIndexStatus> from,
            @Param("to") VectorIndexStatus to);

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :cancelled,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :indexing
               and manifest.updatedAt < :deadline
            """)
    int cancelHangingIndexing(
            @Param("fingerprint") String fingerprint,
            @Param("deadline") LocalDateTime deadline,
            @Param("indexing") VectorIndexStatus indexing,
            @Param("cancelled") VectorIndexStatus cancelled);

    default boolean cancelHangingIndexing(String fingerprint, LocalDateTime deadline) {
        return cancelHangingIndexing(
                fingerprint,
                deadline,
                VectorIndexStatus.INDEXING,
                VectorIndexStatus.CANCELLED) == 1;
    }

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
            """)
    int updateHeartbeat(
            @Param("fingerprint") String fingerprint);

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

    @Modifying(flushAutomatically = true)
    @Query("""
            update VectorIndexManifest manifest
               set manifest.status = :failed,
                   manifest.failureMessage = :failureMessage,
                   manifest.chunkCount = null,
                   manifest.updatedAt = CURRENT_TIMESTAMP
             where manifest.fingerprint = :fingerprint
               and manifest.status = :indexing
               and manifest.updatedAt < :deadline
            """)
    int failHangingIndexing(
            @Param("fingerprint") String fingerprint,
            @Param("failureMessage") String failureMessage,
            @Param("deadline") LocalDateTime deadline,
            @Param("indexing") VectorIndexStatus indexing,
            @Param("failed") VectorIndexStatus failed);
}
