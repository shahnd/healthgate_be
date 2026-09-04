-- ============================================
-- HealthGate 로컬 개발 데이터
-- LocalDatabaseReset이 파일명 순서대로 한 번씩 실행합니다.
-- ============================================

-- ------------------------------
-- 0. risk threshold settings
-- ------------------------------

INSERT INTO risk_threshold_settings (metric_name, risk_level, threshold_value) VALUES
('SYSTOLIC_BP', 'HIGH', 140),
('SYSTOLIC_BP', 'WARN', 130),
('DIASTOLIC_BP', 'HIGH', 90),
('DIASTOLIC_BP', 'WARN', 80),
('HEART_RATE', 'HIGH', 90),
('HEART_RATE', 'WARN', 80);
