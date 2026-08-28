package com.kh.healthgate.safety.domain;

import java.time.LocalDateTime;

import com.kh.healthgate.employee.model.vo.Employee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "safety_documents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SafetyDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "original_filename", nullable = false, length = 512)
    private String originalFilename;

    @Column(name = "storage_key", nullable = false, unique = true, length = 512)
    private String storageKey;

    @Column(name = "content_type", nullable = false, length = 100)
    private String contentType;

    @Column(name = "file_size", nullable = false)
    private long fileSize;

    @Column(name = "content_checksum", nullable = false, unique = true, length = 128)
    private String contentChecksum;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_employee_id", nullable = false)
    private Employee createdBy;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "updated_by_employee_id", nullable = false)
    private Employee updatedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public SafetyDocument(
            String title,
            String description,
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize,
            String contentChecksum,
            Employee employee) {
        this.title = title;
        this.description = description;
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.contentChecksum = contentChecksum;
        this.createdBy = employee;
        this.updatedBy = employee;
    }

    public void updateMetadata(String title, String description, Employee employee) {
        this.title = title;
        this.description = description;
        this.updatedBy = employee;
    }

    public void replaceFile(
            String originalFilename,
            String storageKey,
            String contentType,
            long fileSize,
            String contentChecksum,
            Employee employee) {
        this.originalFilename = originalFilename;
        this.storageKey = storageKey;
        this.contentType = contentType;
        this.fileSize = fileSize;
        this.contentChecksum = contentChecksum;
        this.updatedBy = employee;
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
}
