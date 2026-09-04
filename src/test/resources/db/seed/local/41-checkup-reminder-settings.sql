-- ------------------------------
-- 8. checkup reminders settings
-- ------------------------------
INSERT IGNORE INTO checkup_reminder_settings (checkup_reminder_setting_type, checkup_reminder_setting_message_template, checkup_reminder_setting_cron_schedule, checkup_reminder_setting_is_active)
VALUES
('BEFORE_CHECKUP', '검진 7일 전입니다. 건강검진 일정을 확인해 주세요.', '0 0 9 * * 1', 1),
('MISSING_CHECKUP', '미검진 상태입니다. 빠른 시일 내에 검진을 예약해 주세요.', '0 0 9 * * 3', 1),
('AFTER_CHECKUP', '검진 결과를 확인해 주세요. 건강 관리에 참고하겠습니다.', '0 0 10 * * 5', 0);

