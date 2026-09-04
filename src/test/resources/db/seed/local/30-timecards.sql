-- ------------------------------
-- 5. timecards
-- ------------------------------
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-09-01 08:55:00', 1),
('ATTENDANCE', '2026-09-01 08:50:00', 2),
('LEAVE', '2026-09-01 18:10:00', 3),
('ATTENDANCE', '2026-09-01 09:05:00', 4),
('ATTENDANCE', '2026-09-01 08:40:00', 5);

