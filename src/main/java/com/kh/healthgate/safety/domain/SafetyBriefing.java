package com.kh.healthgate.safety.domain;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "safety_briefings",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_safety_briefings_date_context",
                columnNames = { "briefing_date", "context_fingerprint" }))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SafetyBriefing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "briefing_date", nullable = false)
    private LocalDate briefingDate;

    @Column(name = "context_fingerprint", length = 64, nullable = false)
    private String contextFingerprint;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    public SafetyBriefing(LocalDate briefingDate, String contextFingerprint, String content) {
        this.briefingDate = briefingDate;
        this.contextFingerprint = contextFingerprint;
        this.content = content;
    }
}
