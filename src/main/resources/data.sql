-- ============================================
-- HealthGate 초기 더미데이터
-- 실행 순서: departments -> positions -> employee (FK 순서 고려)
--
-- INSERT IGNORE 사용 이유:
--   departments/positions는 PK(id) 중복 시, employee는 employee_number(unique) 중복 시
--   에러 없이 조용히 skip 됩니다. 즉 서버를 몇 번을 재시작해도 이미 있는 행은
--   다시 쌓이지 않고, 새로 추가한 INSERT 문만 반영됩니다.
-- ============================================

-- ------------------------------
-- 0. risk threshold settings
-- ------------------------------

INSERT INTO risk_threshold_settings (metric_name, risk_level, value) VALUES
('SYSTOLIC_BP', 'HIGH', 140),
('SYSTOLIC_BP', 'WARN', 130),
('DIASTOLIC_BP', 'HIGH', 90),
('DIASTOLIC_BP', 'WARN', 80),
('HEART_RATE', 'HIGH', 90),
('HEART_RATE', 'WARN', 80);

-- ------------------------------
-- 1. departments
-- ------------------------------
INSERT IGNORE INTO departments (id, name) VALUES (1, '인사팀');
INSERT IGNORE INTO departments (id, name) VALUES (2, '총무팀');
INSERT IGNORE INTO departments (id, name) VALUES (3, '안전보건팀');
INSERT IGNORE INTO departments (id, name) VALUES (4, '생산1팀');
INSERT IGNORE INTO departments (id, name) VALUES (5, '생산2팀');
INSERT IGNORE INTO departments (id, name) VALUES (6, '품질관리팀');
INSERT IGNORE INTO departments (id, name) VALUES (7, '영업팀');
INSERT IGNORE INTO departments (id, name) VALUES (8, '전산팀');

-- ------------------------------
-- 2. positions
-- ------------------------------
INSERT IGNORE INTO positions (id, name) VALUES (1, '사원');
INSERT IGNORE INTO positions (id, name) VALUES (2, '주임');
INSERT IGNORE INTO positions (id, name) VALUES (3, '대리');
INSERT IGNORE INTO positions (id, name) VALUES (4, '과장');
INSERT IGNORE INTO positions (id, name) VALUES (5, '차장');
INSERT IGNORE INTO positions (id, name) VALUES (6, '부장');
INSERT IGNORE INTO positions (id, name) VALUES (7, '이사');

-- ------------------------------
-- 3. employee
--    role: EMPLOYEE / HR_ADMIN / HEALTH_ADMIN
--    status: Y(재직) / N(퇴사)
--    password는 전부 평문 '1234' 더미값입니다.
--    실제 로그인 테스트 시 BCryptPasswordEncoder로 인코딩한 값으로 교체하세요.
-- ------------------------------

-- 관리자 계정 (HR_ADMIN, HEALTH_ADMIN)
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('admin1', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '김인사', '2018-03-02', 'kim.hr@healthgate.com', '010-1111-0001', 'HR_ADMIN', 'Y', 1, 6);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('admin2', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '박보건', '2019-05-13', 'park.health@healthgate.com', '010-1111-0002', 'HEALTH_ADMIN', 'Y', 3, 5);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('admin3', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '이산업', '2017-01-10', 'lee.health@healthgate.com', '010-1111-0003', 'HEALTH_ADMIN', 'Y', 3, 6);

-- 일반 사원 (EMPLOYEE)
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp01', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '최민준', '2020-01-06', 'choi.mj@healthgate.com', '010-2000-1001', 'EMPLOYEE', 'Y', 4, 1);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp02', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '정서연', '2020-02-17', 'jung.sy@healthgate.com', '010-2000-1002', 'EMPLOYEE', 'Y', 4, 2);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp03', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '강도윤', '2019-07-01', 'kang.dy@healthgate.com', '010-2000-1003', 'EMPLOYEE', 'Y', 4, 3);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp04', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '윤하은', '2021-03-15', 'yoon.he@healthgate.com', '010-2000-1004', 'EMPLOYEE', 'Y', 5, 1);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp05', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '임지호', '2018-11-20', 'lim.jh@healthgate.com', '010-2000-1005', 'EMPLOYEE', 'Y', 5, 4);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp06', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '한소율', '2022-06-01', 'han.sy@healthgate.com', '010-2000-1006', 'EMPLOYEE', 'Y', 5, 1);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp07', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '오은우', '2016-09-05', 'oh.eu@healthgate.com', '010-2000-1007', 'EMPLOYEE', 'Y', 6, 5);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp08', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '서예은', '2021-01-11', 'seo.ye@healthgate.com', '010-2000-1008', 'EMPLOYEE', 'Y', 6, 2);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp09', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '신유準', '2019-04-22', 'shin.yj@healthgate.com', '010-2000-1009', 'EMPLOYEE', 'Y', 7, 3);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp10', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '홍지안', '2020-08-03', 'hong.ja@healthgate.com', '010-2000-1010', 'EMPLOYEE', 'Y', 7, 1);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp11', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '문시우', '2017-12-18', 'moon.su@healthgate.com', '010-2000-1011', 'EMPLOYEE', 'Y', 8, 4);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp12', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '배아윤', '2023-02-27', 'bae.ay@healthgate.com', '010-2000-1012', 'EMPLOYEE', 'Y', 8, 1);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp13', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '조현우', '2015-05-30', 'jo.hw@healthgate.com', '010-2000-1013', 'EMPLOYEE', 'Y', 2, 6);

INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp14', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '권나은', '2022-10-14', 'kwon.ne@healthgate.com', '010-2000-1014', 'EMPLOYEE', 'Y', 2, 1);

-- 퇴사자 (status = N) 예시
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp15', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '남건우', '2014-03-03', 'nam.gw@healthgate.com', '010-2000-1015', 'EMPLOYEE', 'N', 4, 3);

-- ------------------------------
-- 4. hospitals
-- ------------------------------
INSERT IGNORE INTO hospitals (name, address, phone, url, description, is_general_exam_available, is_stomach_cancer_exam_available, is_colon_cancer_exam_available, is_liver_cancer_exam_available, is_lung_cancer_exam_available, status)
VALUES
('강남메디컬센터', '서울특별시 강남구 테헤란로 123', '02-555-1111', 'https://gangnam-medical.example', '종합검진 및 내과 전문 진료를 제공하는 의료기관입니다.', 1, 1, 1, 1, 1, 'Y'),
('서울건강검진병원', '서울특별시 서초구 반포대로 45', '02-555-2222', 'https://seoul-health.example', '일반검진, 위암, 대장암, 간암 검사를 수행합니다.', 1, 1, 1, 1, 1, 'Y'),
('부산의료원', '부산광역시 해운대구 해운대로 88', '051-555-3333', 'https://busan-med.example', '산업안전검진과 정기 건강검진을 전문으로 합니다.', 1, 1, 1, 0, 1, 'Y');

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

-- ------------------------------
-- 7. checkups
-- ------------------------------
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2026, '2026-06-15', '혈압 정상, 체중 유지, 간 기능 검사 양호', '2026-06-16 09:00:00', 1),
(2026, '2026-07-02', '혈압 경계, 생활습관 개선 권고', '2026-07-03 10:15:00', 2),
(2026, NULL, '검진 미실시 상태', '2026-08-10 11:00:00', 3),
(2026, '2026-05-20', '정상 범위, 특이 소견 없음', '2026-05-21 08:30:00', 4),
(2026, '2026-06-28', '심전도 검사는 정상, 피로 회복 권고', '2026-06-29 09:40:00', 5);

-- ------------------------------
-- 8. checkup reminders settings
-- ------------------------------
INSERT IGNORE INTO checkup_reminder_settings (checkup_reminder_setting_type, checkup_reminder_setting_message_template, checkup_reminder_setting_cron_schedule, checkup_reminder_setting_is_active)
VALUES
('BEFORE_CHECKUP', '검진 7일 전입니다. 건강검진 일정을 확인해 주세요.', '0 9 * * 1', 1),
('MISSING_CHECKUP', '미검진 상태입니다. 빠른 시일 내에 검진을 예약해 주세요.', '0 9 * * 3', 1),
('AFTER_CHECKUP', '검진 결과를 확인해 주세요. 건강 관리에 참고하겠습니다.', '0 10 * * 5', 0);

-- ------------------------------
-- 9. checkup reminders
-- ------------------------------
INSERT IGNORE INTO checkup_reminders (checkup_reminder_channel, checkup_reminder_content, checkup_reminder_sent_at, checkup_reminder_status, checkup_reminder_is_manual, checkup_id)
VALUES
('SMS', '검진 7일 전 안내 메시지입니다.', '2026-08-20 09:00:00', 'SUCCESS', 0, 1),
('EMAIL', '미검진으로 인한 안내 메일입니다.', '2026-08-22 13:30:00', 'SUCCESS', 0, 3),
('SMS', '검진 결과 확인 문자를 발송합니다.', '2026-08-25 10:00:00', 'SUCCESS', 1, 2);

-- ------------------------------
-- 10. consultations
-- ------------------------------
INSERT IGNORE INTO consultations (employee_id, manager_id, scheduled_date, scheduled_turn, reason, content, status, consultated_at, created_at)
VALUES
(1, 2, '2026-09-10', 'T1', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'RESERVED', NULL, '2026-09-01 09:30:00'),
(4, 2, '2026-08-29', 'T2', '혈압 관리 상담', '최근 혈압이 높아져 생활습관 점검이 필요합니다.', 'FINISHED', '2026-08-29 15:00:00', '2026-08-20 09:15:00'),
(5, 2, '2026-09-12', 'T3', '휴식 및 회복 상담', '근무 중 피로감이 높아 상담 요청드립니다.', 'CANCELED', NULL, '2026-09-01 10:00:00');

-- ------------------------------
-- 11. notices
-- ------------------------------
INSERT IGNORE INTO notices (title, content, status, created_at, update_at, author_id, count)
VALUES
('9월 건강검진 일정 안내', '9월 건강검진 예약 일정과 대상자를 안내드립니다. 관련 공지 확인 후 빠르게 예약해 주세요.', 'Y', '2026-09-01 08:00:00', '2026-09-01 08:00:00', 1, 42),
('직장 건강관리 프로그램 운영 안내', '직장 내 건강관리 프로그램을 운영합니다. 참여를 희망하는 직원은 담당 부서로 신청해 주세요.', 'Y', '2026-09-02 09:30:00', '2026-09-02 09:30:00', 2, 18),
('근무 시간 및 휴식 관리 기준 안내', '근무시간 준수와 적절한 휴식 시간을 확보할 수 있도록 기준을 안내합니다.', 'Y', '2026-09-03 13:00:00', '2026-09-03 13:00:00', 3, 27);

-- ------------------------------
-- 12. notice files
-- ------------------------------
INSERT IGNORE INTO notice_files (origin_name, saved_name, saved_path, extension, notice_id)
VALUES
('9월-검진-안내.pdf', '9m-checkup-guide-01.pdf', '/uploads/notices/healthgate', 'pdf', 1),
('직장-건강관리-프로그램.hwp', 'work-health-program-02.hwp', '/uploads/notices/healthgate', 'hwp', 2),
('근무시간-기준안내.docx', 'working-hours-guide-03.docx', '/uploads/notices/healthgate', 'docx', 3);

