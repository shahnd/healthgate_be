package com.kh.healthgate.safety.ai.model.vo;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "vector_index_manifests")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VectorIndexManifest {
    @Id
    @Column(name = "fingerprint", length = 128, nullable = false)
    private String fingerprint;

    @Column(name = "source_name", length = 512, nullable = false)
    private String sourceName;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20, nullable = false)
    private VectorIndexStatus status;

    public VectorIndexManifest(String fingerprint, String sourceName) {
        this.fingerprint = fingerprint;
        this.sourceName = sourceName;
        this.status = VectorIndexStatus.INDEXING;
    }

    public void restart(String sourceName) {
        this.sourceName = sourceName;
        this.status = VectorIndexStatus.INDEXING;
    }

    public void complete() {
        this.status = VectorIndexStatus.COMPLETED;
    }

    public boolean isCompleted() {
        return status == VectorIndexStatus.COMPLETED;
    }
}
