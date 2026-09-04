-- ------------------------------
-- 10. consultations
-- ------------------------------
INSERT IGNORE INTO consultations (employee_id, manager_id, scheduled_date, scheduled_turn, reason, content, status, consultated_at, created_at)
VALUES
(1, 2, '2026-09-10', 'T1', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'RESERVED', NULL, '2026-09-01 09:30:00'),
(4, 2, '2026-08-29', 'T2', '혈압 관리 상담', '최근 혈압이 높아져 생활습관 점검이 필요합니다.', 'FINISHED', '2026-08-29 15:00:00', '2026-08-20 09:15:00'),
(5, 2, '2026-09-12', 'T3', '휴식 및 회복 상담', '근무 중 피로감이 높아 상담 요청드립니다.', 'CANCELED', NULL, '2026-09-01 10:00:00');

