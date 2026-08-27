package com.kh.healthgate.safety.model.dao;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.safety.model.vo.SafetyBriefing;

public interface SafetyBriefingRepository extends JpaRepository<SafetyBriefing, Long> {
    Optional<SafetyBriefing> findByBriefingDateAndContextFingerprint(
            LocalDate briefingDate,
            String contextFingerprint);
}
