-- ------------------------------
-- 6. biometrics
-- ------------------------------
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-09-01 08:30:00', 122, 78, 36.6, 72, 'NORMAL', 1),
('2026-09-01 08:35:00', 138, 88, 36.8, 76, 'WARN', 2),
('2026-09-01 08:40:00', 146, 94, 37.1, 82, 'HIGH', 3),
('2026-09-01 08:45:00', 128, 82, 36.7, 70, 'NORMAL', 4),
('2026-09-01 08:50:00', 132, 84, 36.9, 74, 'WARN', 5);

