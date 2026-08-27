package com.kh.healthgate.safety.ai.model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.kh.healthgate.safety.ai.model.vo.VectorIndexManifest;

public interface VectorIndexManifestRepository extends JpaRepository<VectorIndexManifest, String> {
}
