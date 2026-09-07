package com.kh.healthgate.safety.ai.index;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vector_index_manifests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VectorIndexManifest {
    private static final int MAX_FAILURE_MESSAGE_LENGTH = 1000;

    @Id
    @Column(name = "fingerprint", length = 128, nullable = false)
    private String fingerprint;

    @Column(name = "content_checksum", length = 128, nullable = false)
    private String contentChecksum;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VectorIndexStatus status;

    @Column(name = "failure_message", length = MAX_FAILURE_MESSAGE_LENGTH)
    private String failureMessage;

    @Column(name = "chunk_count")
    private Integer chunkCount;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public VectorIndexManifest(String fingerprint, String contentChecksum) {
        this.fingerprint = fingerprint;
        this.contentChecksum = contentChecksum;
        this.status = VectorIndexStatus.PENDING;
    }

    public void start() {
        this.status = VectorIndexStatus.INDEXING;
        this.failureMessage = null;
        this.chunkCount = null;
    }

    public void complete(int chunkCount) {
        this.status = VectorIndexStatus.COMPLETED;
        this.failureMessage = null;
        this.chunkCount = chunkCount;
    }

    public void fail(String failureMessage) {
        this.status = VectorIndexStatus.FAILED;
        this.failureMessage = truncate(failureMessage);
        this.chunkCount = null;
    }

    public void startPurging() {
        this.status = VectorIndexStatus.PURGING;
        this.failureMessage = null;
    }

    public void failPurging(String failureMessage) {
        this.status = VectorIndexStatus.PURGE_FAILED;
        this.failureMessage = truncate(failureMessage);
    }

    public boolean isCompleted() {
        return status == VectorIndexStatus.COMPLETED;
    }

    @PrePersist
    void created() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updated() {
        updatedAt = LocalDateTime.now();
    }

    private String truncate(String message) {
        if (message == null || message.length() <= MAX_FAILURE_MESSAGE_LENGTH) {
            return message;
        }
        return message.substring(0, MAX_FAILURE_MESSAGE_LENGTH);
    }
}
