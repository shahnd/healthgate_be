package com.kh.healthgate.safety.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.safety.domain.SafetyBriefing;

public interface SafetyBriefingRepository extends JpaRepository<SafetyBriefing, Long> {
    Optional<SafetyBriefing> findByBriefingDateAndContextFingerprint(
            LocalDate briefingDate,
            String contextFingerprint);
}
