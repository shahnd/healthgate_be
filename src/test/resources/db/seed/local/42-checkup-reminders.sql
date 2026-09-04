-- ------------------------------
-- 9. checkup reminders
-- ------------------------------
INSERT IGNORE INTO checkup_reminders (checkup_reminder_channel, checkup_reminder_content, checkup_reminder_sent_at, checkup_reminder_status, checkup_reminder_is_manual, checkup_id)
VALUES
('SMS', '검진 7일 전 안내 메시지입니다.', '2026-08-20 09:00:00', 'SUCCESS', 0, 1),
('EMAIL', '미검진으로 인한 안내 메일입니다.', '2026-08-22 13:30:00', 'SUCCESS', 0, 3),
('SMS', '검진 결과 확인 문자를 발송합니다.', '2026-08-25 10:00:00', 'SUCCESS', 1, 2);

