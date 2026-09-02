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

INSERT INTO risk_threshold_settings (metric_name, risk_level, threshold_value) VALUES
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

-- ============================================
-- HealthGate 추가 더미데이터 (대량, 페이지네이션/필터 테스트용)
-- 기존 dummy.sql 실행 이후에 실행하세요.
-- ============================================

-- ------------------------------
-- 3-1. 추가 직원 (emp16 ~ emp35, 페이지네이션/검색 테스트용)
-- ------------------------------
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp16', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '최민서', '2019-04-08', 'emp16@healthgate.com', '010-3000-1016', 'EMPLOYEE', 'Y', 3, 6);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp17', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '송도현', '2024-07-02', 'emp17@healthgate.com', '010-3000-1017', 'EMPLOYEE', 'Y', 1, 1);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp18', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '안태윤', '2015-09-07', 'emp18@healthgate.com', '010-3000-1018', 'EMPLOYEE', 'Y', 7, 2);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp19', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장민서', '2017-12-14', 'emp19@healthgate.com', '010-3000-1019', 'EMPLOYEE', 'Y', 6, 3);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp20', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '한지우', '2016-07-04', 'emp20@healthgate.com', '010-3000-1020', 'EMPLOYEE', 'Y', 6, 7);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp21', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장서준', '2022-09-04', 'emp21@healthgate.com', '010-3000-1021', 'EMPLOYEE', 'Y', 7, 1);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp22', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '홍건우', '2024-04-23', 'emp22@healthgate.com', '010-3000-1022', 'EMPLOYEE', 'Y', 2, 1);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp23', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '임도현', '2018-02-13', 'emp23@healthgate.com', '010-3000-1023', 'EMPLOYEE', 'Y', 5, 4);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp24', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '오예준', '2020-06-07', 'emp24@healthgate.com', '010-3000-1024', 'EMPLOYEE', 'Y', 5, 6);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp25', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '박태윤', '2017-09-24', 'emp25@healthgate.com', '010-3000-1025', 'EMPLOYEE', 'Y', 4, 2);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp26', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장현우', '2018-11-11', 'emp26@healthgate.com', '010-3000-1026', 'EMPLOYEE', 'Y', 1, 2);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp27', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '한다은', '2019-02-07', 'emp27@healthgate.com', '010-3000-1027', 'EMPLOYEE', 'Y', 6, 2);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp28', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '서나은', '2017-05-05', 'emp28@healthgate.com', '010-3000-1028', 'EMPLOYEE', 'Y', 4, 6);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp29', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장예린', '2021-10-13', 'emp29@healthgate.com', '010-3000-1029', 'EMPLOYEE', 'Y', 6, 2);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp30', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '정수빈', '2022-02-25', 'emp30@healthgate.com', '010-3000-1030', 'EMPLOYEE', 'Y', 1, 7);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp31', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '강우진', '2024-02-13', 'emp31@healthgate.com', '010-3000-1031', 'EMPLOYEE', 'Y', 7, 5);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp32', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '안채원', '2023-01-22', 'emp32@healthgate.com', '010-3000-1032', 'EMPLOYEE', 'Y', 2, 6);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp33', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장유진', '2016-05-14', 'emp33@healthgate.com', '010-3000-1033', 'EMPLOYEE', 'N', 3, 4);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp34', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '장수빈', '2017-09-04', 'emp34@healthgate.com', '010-3000-1034', 'EMPLOYEE', 'Y', 5, 7);
INSERT IGNORE INTO employees (employee_number, password, name, hire_date, email, phone, role, status, department_id, position_id)
VALUES ('emp35', '$2a$10$kXm11MD.jblEMM2c.PmUau/mdaRqnJ4OvNRW1rYbysFfcReTn5sKC', '홍수아', '2017-06-25', 'emp35@healthgate.com', '010-3000-1035', 'EMPLOYEE', 'Y', 3, 5);

-- ------------------------------
-- 5-1. timecards 추가 (최근 14일치, 직원 1~35)
-- ------------------------------
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-09-01 08:38:00', 1),
('LEAVE', '2026-08-31 08:07:00', 1),
('ATTENDANCE', '2026-08-28 09:15:00', 1),
('ATTENDANCE', '2026-08-27 08:05:00', 1),
('ATTENDANCE', '2026-08-26 08:48:00', 1),
('ATTENDANCE', '2026-08-25 08:08:00', 1),
('ATTENDANCE', '2026-08-24 08:16:00', 1),
('LEAVE', '2026-08-21 09:13:00', 1),
('ATTENDANCE', '2026-08-20 08:45:00', 1),
('LEAVE', '2026-08-19 09:28:00', 1),
('ATTENDANCE', '2026-09-01 08:15:00', 2),
('ATTENDANCE', '2026-08-31 09:01:00', 2),
('ATTENDANCE', '2026-08-28 08:37:00', 2),
('ATTENDANCE', '2026-08-27 08:45:00', 2),
('LEAVE', '2026-08-26 08:04:00', 2),
('ATTENDANCE', '2026-08-25 09:04:00', 2),
('ATTENDANCE', '2026-08-24 09:42:00', 2),
('LEAVE', '2026-08-21 08:46:00', 2),
('ATTENDANCE', '2026-08-20 09:15:00', 2),
('ATTENDANCE', '2026-09-01 08:06:00', 3);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-31 09:22:00', 3),
('ATTENDANCE', '2026-08-28 09:55:00', 3),
('ATTENDANCE', '2026-08-27 08:03:00', 3),
('LEAVE', '2026-08-26 09:51:00', 3),
('ATTENDANCE', '2026-08-25 08:12:00', 3),
('ATTENDANCE', '2026-08-24 09:08:00', 3),
('ATTENDANCE', '2026-08-21 09:29:00', 3),
('ATTENDANCE', '2026-08-20 08:28:00', 3),
('ATTENDANCE', '2026-09-01 08:03:00', 4),
('LEAVE', '2026-08-31 08:05:00', 4),
('ATTENDANCE', '2026-08-28 08:10:00', 4),
('LEAVE', '2026-08-27 09:13:00', 4),
('ATTENDANCE', '2026-08-26 08:10:00', 4),
('LEAVE', '2026-08-25 09:16:00', 4),
('ATTENDANCE', '2026-08-24 09:18:00', 4),
('ATTENDANCE', '2026-08-21 09:09:00', 4),
('ATTENDANCE', '2026-08-20 08:03:00', 4),
('ATTENDANCE', '2026-08-19 08:47:00', 4),
('LEAVE', '2026-09-01 09:32:00', 5),
('LEAVE', '2026-08-31 08:03:00', 5);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-28 08:54:00', 5),
('LEAVE', '2026-08-27 08:43:00', 5),
('LEAVE', '2026-08-26 09:07:00', 5),
('ATTENDANCE', '2026-08-25 08:37:00', 5),
('ATTENDANCE', '2026-08-24 08:26:00', 5),
('ATTENDANCE', '2026-09-01 09:59:00', 6),
('ATTENDANCE', '2026-08-31 09:15:00', 6),
('ATTENDANCE', '2026-08-28 08:42:00', 6),
('LEAVE', '2026-08-27 09:20:00', 6),
('ATTENDANCE', '2026-08-26 08:00:00', 6),
('ATTENDANCE', '2026-08-25 08:04:00', 6),
('LEAVE', '2026-08-24 09:08:00', 6),
('ATTENDANCE', '2026-08-21 08:56:00', 6),
('ATTENDANCE', '2026-08-20 09:10:00', 6),
('LEAVE', '2026-08-19 09:39:00', 6),
('ATTENDANCE', '2026-09-01 08:42:00', 7),
('ATTENDANCE', '2026-08-31 09:59:00', 7),
('ATTENDANCE', '2026-08-28 08:16:00', 7),
('ATTENDANCE', '2026-08-27 08:47:00', 7),
('ATTENDANCE', '2026-08-26 09:18:00', 7);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-25 09:13:00', 7),
('ATTENDANCE', '2026-08-24 09:32:00', 7),
('ATTENDANCE', '2026-08-21 08:05:00', 7),
('ATTENDANCE', '2026-08-20 09:02:00', 7),
('LEAVE', '2026-08-19 08:40:00', 7),
('ATTENDANCE', '2026-09-01 09:35:00', 8),
('ATTENDANCE', '2026-08-31 08:07:00', 8),
('ATTENDANCE', '2026-08-28 08:34:00', 8),
('ATTENDANCE', '2026-08-27 09:37:00', 8),
('ATTENDANCE', '2026-08-26 09:08:00', 8),
('LEAVE', '2026-08-25 09:57:00', 8),
('ATTENDANCE', '2026-08-24 08:57:00', 8),
('ATTENDANCE', '2026-09-01 08:22:00', 9),
('ATTENDANCE', '2026-08-31 09:39:00', 9),
('ATTENDANCE', '2026-08-28 08:55:00', 9),
('ATTENDANCE', '2026-08-27 08:56:00', 9),
('LEAVE', '2026-08-26 08:47:00', 9),
('ATTENDANCE', '2026-08-25 09:51:00', 9),
('ATTENDANCE', '2026-08-24 08:17:00', 9),
('ATTENDANCE', '2026-09-01 09:55:00', 10);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-31 09:14:00', 10),
('ATTENDANCE', '2026-08-28 09:22:00', 10),
('ATTENDANCE', '2026-08-27 08:14:00', 10),
('ATTENDANCE', '2026-08-26 08:25:00', 10),
('ATTENDANCE', '2026-08-25 08:49:00', 10),
('LEAVE', '2026-08-24 09:43:00', 10),
('ATTENDANCE', '2026-09-01 09:01:00', 11),
('ATTENDANCE', '2026-08-31 09:11:00', 11),
('ATTENDANCE', '2026-08-28 09:02:00', 11),
('ATTENDANCE', '2026-08-27 09:22:00', 11),
('ATTENDANCE', '2026-08-26 09:27:00', 11),
('LEAVE', '2026-08-25 08:24:00', 11),
('ATTENDANCE', '2026-08-24 08:16:00', 11),
('ATTENDANCE', '2026-08-21 09:00:00', 11),
('ATTENDANCE', '2026-08-20 08:23:00', 11),
('ATTENDANCE', '2026-08-19 09:39:00', 11),
('ATTENDANCE', '2026-09-01 09:32:00', 12),
('ATTENDANCE', '2026-08-31 09:20:00', 12),
('ATTENDANCE', '2026-08-28 09:35:00', 12),
('LEAVE', '2026-08-27 09:42:00', 12);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-26 08:39:00', 12),
('ATTENDANCE', '2026-08-25 09:35:00', 12),
('ATTENDANCE', '2026-08-24 09:18:00', 12),
('ATTENDANCE', '2026-09-01 09:29:00', 13),
('ATTENDANCE', '2026-08-31 08:32:00', 13),
('ATTENDANCE', '2026-08-28 08:42:00', 13),
('ATTENDANCE', '2026-08-27 09:05:00', 13),
('ATTENDANCE', '2026-08-26 08:43:00', 13),
('ATTENDANCE', '2026-08-25 08:09:00', 13),
('ATTENDANCE', '2026-08-24 08:30:00', 13),
('ATTENDANCE', '2026-08-21 08:29:00', 13),
('ATTENDANCE', '2026-08-20 08:45:00', 13),
('ATTENDANCE', '2026-08-19 09:25:00', 13),
('ATTENDANCE', '2026-09-01 08:49:00', 14),
('LEAVE', '2026-08-31 08:51:00', 14),
('ATTENDANCE', '2026-08-28 09:03:00', 14),
('ATTENDANCE', '2026-08-27 08:29:00', 14),
('ATTENDANCE', '2026-08-26 09:42:00', 14),
('LEAVE', '2026-08-25 09:48:00', 14),
('LEAVE', '2026-08-24 09:53:00', 14);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('LEAVE', '2026-09-01 08:47:00', 16),
('ATTENDANCE', '2026-08-31 09:16:00', 16),
('ATTENDANCE', '2026-08-28 09:49:00', 16),
('ATTENDANCE', '2026-08-27 09:40:00', 16),
('ATTENDANCE', '2026-08-26 09:04:00', 16),
('ATTENDANCE', '2026-08-25 08:17:00', 16),
('ATTENDANCE', '2026-08-24 08:08:00', 16),
('ATTENDANCE', '2026-08-21 09:44:00', 16),
('ATTENDANCE', '2026-08-20 08:04:00', 16),
('ATTENDANCE', '2026-09-01 09:26:00', 17),
('LEAVE', '2026-08-31 09:24:00', 17),
('LEAVE', '2026-08-28 08:54:00', 17),
('ATTENDANCE', '2026-08-27 09:30:00', 17),
('ATTENDANCE', '2026-08-26 09:19:00', 17),
('ATTENDANCE', '2026-08-25 09:34:00', 17),
('ATTENDANCE', '2026-08-24 08:31:00', 17),
('ATTENDANCE', '2026-08-21 09:31:00', 17),
('ATTENDANCE', '2026-09-01 09:46:00', 18),
('ATTENDANCE', '2026-08-31 09:58:00', 18),
('ATTENDANCE', '2026-08-28 08:58:00', 18);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-27 08:05:00', 18),
('ATTENDANCE', '2026-08-26 08:55:00', 18),
('ATTENDANCE', '2026-08-25 08:16:00', 18),
('ATTENDANCE', '2026-08-24 08:29:00', 18),
('ATTENDANCE', '2026-08-21 09:17:00', 18),
('ATTENDANCE', '2026-09-01 09:53:00', 19),
('ATTENDANCE', '2026-08-31 08:47:00', 19),
('ATTENDANCE', '2026-08-28 09:14:00', 19),
('ATTENDANCE', '2026-08-27 08:48:00', 19),
('ATTENDANCE', '2026-08-26 08:12:00', 19),
('ATTENDANCE', '2026-08-25 08:15:00', 19),
('LEAVE', '2026-08-24 08:36:00', 19),
('ATTENDANCE', '2026-08-21 09:44:00', 19),
('ATTENDANCE', '2026-08-20 09:10:00', 19),
('ATTENDANCE', '2026-09-01 08:19:00', 20),
('ATTENDANCE', '2026-08-31 08:59:00', 20),
('LEAVE', '2026-08-28 09:25:00', 20),
('ATTENDANCE', '2026-08-27 08:04:00', 20),
('ATTENDANCE', '2026-08-26 08:06:00', 20),
('ATTENDANCE', '2026-08-25 09:54:00', 20);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('LEAVE', '2026-08-24 08:50:00', 20),
('ATTENDANCE', '2026-09-01 09:34:00', 21),
('ATTENDANCE', '2026-08-31 09:04:00', 21),
('ATTENDANCE', '2026-08-28 09:00:00', 21),
('ATTENDANCE', '2026-08-27 09:06:00', 21),
('LEAVE', '2026-08-26 09:40:00', 21),
('ATTENDANCE', '2026-08-25 09:45:00', 21),
('ATTENDANCE', '2026-08-24 08:46:00', 21),
('ATTENDANCE', '2026-09-01 09:29:00', 22),
('LEAVE', '2026-08-31 09:20:00', 22),
('LEAVE', '2026-08-28 08:17:00', 22),
('ATTENDANCE', '2026-08-27 08:48:00', 22),
('ATTENDANCE', '2026-08-26 09:21:00', 22),
('ATTENDANCE', '2026-08-25 09:11:00', 22),
('ATTENDANCE', '2026-08-24 09:51:00', 22),
('ATTENDANCE', '2026-08-21 09:56:00', 22),
('LEAVE', '2026-09-01 08:33:00', 23),
('ATTENDANCE', '2026-08-31 08:15:00', 23),
('ATTENDANCE', '2026-08-28 09:35:00', 23),
('ATTENDANCE', '2026-08-27 09:41:00', 23);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-26 09:50:00', 23),
('ATTENDANCE', '2026-08-25 09:14:00', 23),
('ATTENDANCE', '2026-08-24 08:19:00', 23),
('ATTENDANCE', '2026-08-21 09:30:00', 23),
('ATTENDANCE', '2026-09-01 09:47:00', 24),
('ATTENDANCE', '2026-08-31 09:44:00', 24),
('ATTENDANCE', '2026-08-28 09:16:00', 24),
('ATTENDANCE', '2026-08-27 08:20:00', 24),
('ATTENDANCE', '2026-08-26 08:12:00', 24),
('ATTENDANCE', '2026-08-25 09:17:00', 24),
('ATTENDANCE', '2026-08-24 09:06:00', 24),
('ATTENDANCE', '2026-08-21 09:14:00', 24),
('ATTENDANCE', '2026-09-01 08:45:00', 25),
('LEAVE', '2026-08-31 09:02:00', 25),
('LEAVE', '2026-08-28 09:44:00', 25),
('LEAVE', '2026-08-27 09:06:00', 25),
('ATTENDANCE', '2026-08-26 09:30:00', 25),
('LEAVE', '2026-08-25 09:11:00', 25),
('ATTENDANCE', '2026-08-24 09:55:00', 25),
('ATTENDANCE', '2026-08-21 08:25:00', 25);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-09-01 08:09:00', 26),
('LEAVE', '2026-08-31 09:05:00', 26),
('ATTENDANCE', '2026-08-28 08:35:00', 26),
('ATTENDANCE', '2026-08-27 08:49:00', 26),
('ATTENDANCE', '2026-08-26 09:58:00', 26),
('ATTENDANCE', '2026-08-25 09:19:00', 26),
('LEAVE', '2026-08-24 08:39:00', 26),
('ATTENDANCE', '2026-08-21 08:48:00', 26),
('ATTENDANCE', '2026-08-20 08:16:00', 26),
('ATTENDANCE', '2026-08-19 08:15:00', 26),
('ATTENDANCE', '2026-09-01 08:00:00', 27),
('ATTENDANCE', '2026-08-31 09:18:00', 27),
('ATTENDANCE', '2026-08-28 09:45:00', 27),
('ATTENDANCE', '2026-08-27 09:04:00', 27),
('ATTENDANCE', '2026-08-26 09:50:00', 27),
('ATTENDANCE', '2026-08-25 08:27:00', 27),
('ATTENDANCE', '2026-08-24 08:41:00', 27),
('ATTENDANCE', '2026-09-01 08:04:00', 28),
('ATTENDANCE', '2026-08-31 09:38:00', 28),
('ATTENDANCE', '2026-08-28 09:28:00', 28);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-27 09:44:00', 28),
('ATTENDANCE', '2026-08-26 09:32:00', 28),
('ATTENDANCE', '2026-08-25 09:05:00', 28),
('ATTENDANCE', '2026-08-24 09:47:00', 28),
('ATTENDANCE', '2026-08-21 09:01:00', 28),
('ATTENDANCE', '2026-09-01 08:48:00', 29),
('ATTENDANCE', '2026-08-31 09:36:00', 29),
('ATTENDANCE', '2026-08-28 08:30:00', 29),
('ATTENDANCE', '2026-08-27 09:58:00', 29),
('ATTENDANCE', '2026-08-26 09:40:00', 29),
('ATTENDANCE', '2026-08-25 08:30:00', 29),
('ATTENDANCE', '2026-08-24 09:20:00', 29),
('ATTENDANCE', '2026-08-21 08:21:00', 29),
('ATTENDANCE', '2026-08-20 09:18:00', 29),
('ATTENDANCE', '2026-08-19 09:52:00', 29),
('ATTENDANCE', '2026-09-01 09:05:00', 30),
('LEAVE', '2026-08-31 09:07:00', 30),
('ATTENDANCE', '2026-08-28 09:55:00', 30),
('LEAVE', '2026-08-27 08:42:00', 30),
('ATTENDANCE', '2026-08-26 09:26:00', 30);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-25 09:39:00', 30),
('ATTENDANCE', '2026-08-24 09:48:00', 30),
('ATTENDANCE', '2026-09-01 08:59:00', 31),
('ATTENDANCE', '2026-08-31 09:07:00', 31),
('ATTENDANCE', '2026-08-28 08:45:00', 31),
('ATTENDANCE', '2026-08-27 08:35:00', 31),
('LEAVE', '2026-08-26 08:53:00', 31),
('ATTENDANCE', '2026-08-25 09:07:00', 31),
('LEAVE', '2026-08-24 08:31:00', 31),
('ATTENDANCE', '2026-08-21 09:32:00', 31),
('ATTENDANCE', '2026-09-01 09:30:00', 32),
('ATTENDANCE', '2026-08-31 08:24:00', 32),
('ATTENDANCE', '2026-08-28 08:55:00', 32),
('LEAVE', '2026-08-27 09:21:00', 32),
('ATTENDANCE', '2026-08-26 09:52:00', 32),
('ATTENDANCE', '2026-08-25 09:53:00', 32),
('ATTENDANCE', '2026-08-24 09:55:00', 32),
('ATTENDANCE', '2026-08-21 09:22:00', 32),
('LEAVE', '2026-08-20 09:29:00', 32),
('ATTENDANCE', '2026-09-01 08:36:00', 33);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-31 09:02:00', 33),
('LEAVE', '2026-08-28 09:45:00', 33),
('LEAVE', '2026-08-27 09:24:00', 33),
('LEAVE', '2026-08-26 08:31:00', 33),
('LEAVE', '2026-08-25 08:32:00', 33),
('ATTENDANCE', '2026-08-24 09:55:00', 33),
('LEAVE', '2026-09-01 08:33:00', 34),
('ATTENDANCE', '2026-08-31 08:46:00', 34),
('ATTENDANCE', '2026-08-28 08:04:00', 34),
('ATTENDANCE', '2026-08-27 09:21:00', 34),
('ATTENDANCE', '2026-08-26 09:41:00', 34),
('ATTENDANCE', '2026-08-25 09:54:00', 34),
('ATTENDANCE', '2026-08-24 09:20:00', 34),
('ATTENDANCE', '2026-08-21 09:55:00', 34),
('ATTENDANCE', '2026-08-20 08:15:00', 34),
('ATTENDANCE', '2026-09-01 08:47:00', 35),
('ATTENDANCE', '2026-08-31 08:48:00', 35),
('ATTENDANCE', '2026-08-28 08:28:00', 35),
('ATTENDANCE', '2026-08-27 09:57:00', 35),
('ATTENDANCE', '2026-08-26 09:50:00', 35);
INSERT IGNORE INTO timecards (status, clock_in_at, employee_id)
VALUES
('ATTENDANCE', '2026-08-25 09:23:00', 35),
('ATTENDANCE', '2026-08-24 08:33:00', 35),
('ATTENDANCE', '2026-08-21 08:10:00', 35);

-- ------------------------------
-- 6-1. biometrics 추가 (최근 10일치, 직원 1~35)
-- ------------------------------
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-09-01 09:31:00', 142, 74, 36.5, 76, 'HIGH', 1),
('2026-09-01 15:16:00', 147, 65, 37.5, 89, 'HIGH', 1),
('2026-08-31 09:04:00', 133, 87, 37.6, 79, 'WARN', 1),
('2026-08-31 15:44:00', 121, 94, 37.4, 72, 'HIGH', 1),
('2026-08-28 15:06:00', 120, 89, 37.0, 96, 'HIGH', 1),
('2026-08-28 14:44:00', 123, 66, 37.6, 85, 'WARN', 1),
('2026-08-27 08:36:00', 148, 68, 37.5, 91, 'HIGH', 1),
('2026-08-27 14:49:00', 119, 87, 36.5, 72, 'WARN', 1),
('2026-08-26 09:40:00', 111, 67, 36.6, 88, 'WARN', 1),
('2026-08-26 08:37:00', 128, 73, 36.3, 78, 'NORMAL', 1),
('2026-08-25 15:11:00', 117, 73, 37.3, 83, 'WARN', 1),
('2026-08-25 14:53:00', 115, 81, 37.5, 90, 'HIGH', 1),
('2026-08-24 14:51:00', 112, 94, 37.5, 69, 'HIGH', 1),
('2026-08-24 09:55:00', 148, 90, 37.6, 95, 'HIGH', 1),
('2026-09-01 15:00:00', 121, 72, 36.8, 76, 'NORMAL', 2),
('2026-08-31 14:06:00', 148, 79, 36.9, 95, 'HIGH', 2),
('2026-08-31 14:58:00', 144, 79, 37.1, 89, 'HIGH', 2),
('2026-08-28 15:07:00', 113, 67, 37.5, 79, 'NORMAL', 2),
('2026-08-28 15:07:00', 111, 80, 37.4, 68, 'WARN', 2),
('2026-08-27 15:23:00', 147, 91, 37.0, 69, 'HIGH', 2);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-27 15:41:00', 111, 96, 37.1, 77, 'HIGH', 2),
('2026-08-26 14:13:00', 133, 93, 37.5, 83, 'HIGH', 2),
('2026-08-25 14:34:00', 146, 87, 36.3, 77, 'HIGH', 2),
('2026-09-01 15:05:00', 147, 78, 37.1, 98, 'HIGH', 3),
('2026-08-31 08:50:00', 126, 80, 37.6, 96, 'HIGH', 3),
('2026-08-28 08:53:00', 140, 78, 37.0, 74, 'HIGH', 3),
('2026-08-27 09:50:00', 143, 65, 36.6, 69, 'HIGH', 3),
('2026-08-27 09:34:00', 121, 76, 36.4, 61, 'NORMAL', 3),
('2026-08-26 08:22:00', 120, 85, 36.2, 76, 'WARN', 3),
('2026-09-01 15:33:00', 112, 69, 36.9, 83, 'WARN', 4),
('2026-08-31 15:32:00', 119, 67, 37.2, 93, 'HIGH', 4),
('2026-08-28 15:41:00', 106, 68, 37.6, 85, 'WARN', 4),
('2026-08-28 15:43:00', 111, 96, 37.2, 88, 'HIGH', 4),
('2026-08-27 08:20:00', 143, 74, 36.3, 77, 'HIGH', 4),
('2026-09-01 15:38:00', 138, 83, 36.8, 98, 'HIGH', 5),
('2026-09-01 15:06:00', 149, 72, 37.4, 95, 'HIGH', 5),
('2026-08-31 15:28:00', 119, 91, 36.7, 89, 'HIGH', 5),
('2026-08-28 15:46:00', 111, 85, 36.8, 76, 'WARN', 5),
('2026-08-28 14:09:00', 148, 95, 36.3, 65, 'HIGH', 5),
('2026-08-27 15:06:00', 128, 73, 37.0, 97, 'HIGH', 5);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-26 08:26:00', 127, 92, 37.4, 63, 'HIGH', 5),
('2026-08-26 14:38:00', 124, 87, 36.3, 92, 'HIGH', 5),
('2026-08-25 09:42:00', 135, 79, 37.4, 82, 'WARN', 5),
('2026-08-24 08:48:00', 122, 79, 37.3, 95, 'HIGH', 5),
('2026-08-24 08:38:00', 147, 82, 36.2, 77, 'HIGH', 5),
('2026-09-01 14:00:00', 116, 74, 37.0, 85, 'WARN', 6),
('2026-09-01 08:09:00', 145, 66, 36.3, 93, 'HIGH', 6),
('2026-08-31 15:26:00', 134, 86, 36.4, 79, 'WARN', 6),
('2026-08-28 08:56:00', 108, 74, 36.4, 63, 'NORMAL', 6),
('2026-08-28 08:17:00', 133, 92, 36.9, 88, 'HIGH', 6),
('2026-08-27 14:13:00', 137, 72, 36.7, 67, 'WARN', 6),
('2026-08-27 14:43:00', 148, 96, 36.9, 79, 'HIGH', 6),
('2026-08-26 09:25:00', 143, 68, 36.2, 79, 'HIGH', 6),
('2026-08-25 09:48:00', 121, 83, 36.7, 60, 'WARN', 6),
('2026-09-01 09:08:00', 129, 79, 36.9, 82, 'WARN', 7),
('2026-09-01 08:25:00', 107, 92, 36.2, 64, 'HIGH', 7),
('2026-08-31 15:36:00', 130, 91, 36.6, 85, 'HIGH', 7),
('2026-08-31 08:20:00', 115, 94, 37.4, 83, 'HIGH', 7),
('2026-08-28 15:54:00', 111, 80, 36.8, 85, 'WARN', 7),
('2026-08-27 15:55:00', 124, 86, 36.5, 70, 'WARN', 7);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-26 08:33:00', 137, 77, 37.5, 82, 'WARN', 7),
('2026-08-25 09:15:00', 111, 74, 36.6, 71, 'NORMAL', 7),
('2026-08-25 09:48:00', 146, 69, 36.4, 91, 'HIGH', 7),
('2026-08-24 15:43:00', 141, 85, 37.4, 80, 'HIGH', 7),
('2026-08-24 09:28:00', 109, 95, 36.8, 79, 'HIGH', 7),
('2026-09-01 14:32:00', 109, 84, 36.8, 62, 'WARN', 8),
('2026-08-31 14:53:00', 123, 69, 37.1, 65, 'NORMAL', 8),
('2026-08-28 15:37:00', 140, 67, 36.8, 96, 'HIGH', 8),
('2026-08-28 09:20:00', 143, 95, 36.9, 63, 'HIGH', 8),
('2026-08-27 08:51:00', 126, 70, 36.9, 71, 'NORMAL', 8),
('2026-08-27 08:15:00', 150, 93, 37.6, 93, 'HIGH', 8),
('2026-08-26 14:23:00', 123, 89, 36.8, 81, 'WARN', 8),
('2026-08-25 14:04:00', 126, 71, 37.0, 84, 'WARN', 8),
('2026-09-01 09:21:00', 110, 74, 37.5, 79, 'NORMAL', 9),
('2026-09-01 15:08:00', 143, 70, 36.6, 84, 'HIGH', 9),
('2026-08-31 09:42:00', 149, 70, 37.1, 87, 'HIGH', 9),
('2026-08-31 14:01:00', 128, 84, 36.5, 73, 'WARN', 9),
('2026-08-28 15:12:00', 119, 73, 36.4, 78, 'NORMAL', 9),
('2026-08-28 08:32:00', 139, 67, 37.1, 68, 'WARN', 9),
('2026-08-27 09:10:00', 116, 75, 37.2, 62, 'NORMAL', 9);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-27 15:23:00', 148, 80, 37.6, 78, 'HIGH', 9),
('2026-08-26 09:34:00', 120, 84, 37.6, 90, 'HIGH', 9),
('2026-08-26 09:23:00', 148, 93, 36.8, 78, 'HIGH', 9),
('2026-08-25 15:10:00', 117, 73, 37.4, 63, 'NORMAL', 9),
('2026-08-25 15:55:00', 128, 71, 37.2, 93, 'HIGH', 9),
('2026-09-01 08:48:00', 115, 82, 36.8, 92, 'HIGH', 10),
('2026-09-01 09:53:00', 132, 70, 37.5, 74, 'WARN', 10),
('2026-08-31 14:59:00', 106, 91, 36.3, 92, 'HIGH', 10),
('2026-08-31 14:15:00', 129, 70, 36.7, 61, 'NORMAL', 10),
('2026-08-28 08:53:00', 150, 86, 37.3, 68, 'HIGH', 10),
('2026-08-28 08:18:00', 135, 73, 37.3, 90, 'HIGH', 10),
('2026-08-27 08:57:00', 110, 66, 36.6, 69, 'NORMAL', 10),
('2026-08-27 15:07:00', 123, 80, 36.6, 63, 'WARN', 10),
('2026-09-01 15:04:00', 112, 96, 37.0, 61, 'HIGH', 11),
('2026-09-01 09:45:00', 114, 83, 36.8, 82, 'WARN', 11),
('2026-08-31 15:11:00', 147, 70, 36.9, 83, 'HIGH', 11),
('2026-08-28 08:24:00', 135, 67, 37.1, 83, 'WARN', 11),
('2026-08-27 08:22:00', 109, 87, 36.5, 66, 'WARN', 11),
('2026-08-27 14:08:00', 107, 87, 37.0, 71, 'WARN', 11),
('2026-08-26 15:40:00', 116, 73, 36.3, 89, 'WARN', 11);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-26 08:18:00', 117, 67, 37.3, 62, 'NORMAL', 11),
('2026-09-01 15:52:00', 139, 95, 36.6, 72, 'HIGH', 12),
('2026-09-01 14:22:00', 108, 86, 36.6, 83, 'WARN', 12),
('2026-08-31 15:47:00', 133, 89, 36.7, 71, 'WARN', 12),
('2026-08-31 15:44:00', 136, 88, 37.5, 93, 'HIGH', 12),
('2026-08-28 08:46:00', 132, 70, 36.8, 71, 'WARN', 12),
('2026-08-28 14:20:00', 111, 70, 36.7, 78, 'NORMAL', 12),
('2026-08-27 15:38:00', 150, 92, 36.4, 88, 'HIGH', 12),
('2026-08-27 14:28:00', 107, 87, 37.1, 87, 'WARN', 12),
('2026-08-26 08:04:00', 147, 90, 36.7, 70, 'HIGH', 12),
('2026-08-26 08:09:00', 143, 93, 36.2, 64, 'HIGH', 12),
('2026-08-25 14:23:00', 129, 67, 37.0, 88, 'WARN', 12),
('2026-09-01 15:48:00', 109, 73, 36.9, 85, 'WARN', 13),
('2026-09-01 14:41:00', 122, 80, 37.5, 61, 'WARN', 13),
('2026-08-31 15:33:00', 129, 72, 36.6, 76, 'NORMAL', 13),
('2026-08-28 09:39:00', 123, 96, 36.5, 68, 'HIGH', 13),
('2026-08-28 08:28:00', 116, 93, 37.6, 80, 'HIGH', 13),
('2026-08-27 08:35:00', 139, 83, 37.4, 70, 'WARN', 13),
('2026-08-27 09:50:00', 128, 79, 36.4, 72, 'NORMAL', 13),
('2026-08-26 09:50:00', 136, 66, 36.7, 96, 'HIGH', 13);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-25 15:51:00', 140, 73, 37.1, 65, 'HIGH', 13),
('2026-08-25 08:19:00', 130, 95, 36.9, 86, 'HIGH', 13),
('2026-09-01 09:20:00', 146, 69, 36.8, 93, 'HIGH', 14),
('2026-08-31 09:56:00', 140, 76, 37.3, 68, 'HIGH', 14),
('2026-08-31 15:32:00', 108, 72, 36.9, 79, 'NORMAL', 14),
('2026-08-28 09:20:00', 150, 79, 36.7, 93, 'HIGH', 14),
('2026-08-27 08:16:00', 117, 82, 36.4, 79, 'WARN', 14),
('2026-08-27 08:32:00', 146, 75, 37.0, 97, 'HIGH', 14),
('2026-08-26 09:42:00', 144, 86, 37.4, 96, 'HIGH', 14),
('2026-08-25 08:05:00', 107, 81, 37.1, 96, 'HIGH', 14),
('2026-08-24 08:31:00', 145, 83, 37.1, 79, 'HIGH', 14),
('2026-08-24 15:15:00', 148, 90, 36.6, 64, 'HIGH', 14),
('2026-09-01 15:26:00', 135, 94, 36.5, 98, 'HIGH', 16),
('2026-08-31 14:55:00', 150, 85, 37.2, 82, 'HIGH', 16),
('2026-08-28 09:48:00', 128, 71, 36.6, 89, 'WARN', 16),
('2026-08-28 08:17:00', 133, 80, 36.4, 63, 'WARN', 16),
('2026-08-27 15:55:00', 144, 91, 36.5, 70, 'HIGH', 16),
('2026-08-27 14:59:00', 141, 85, 36.5, 70, 'HIGH', 16),
('2026-09-01 15:56:00', 124, 96, 36.2, 85, 'HIGH', 17),
('2026-09-01 15:15:00', 118, 87, 36.3, 78, 'WARN', 17);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-31 15:18:00', 139, 65, 37.4, 87, 'WARN', 17),
('2026-08-31 09:56:00', 121, 88, 37.3, 83, 'WARN', 17),
('2026-08-28 15:03:00', 141, 77, 36.7, 78, 'HIGH', 17),
('2026-08-27 15:32:00', 133, 82, 37.4, 67, 'WARN', 17),
('2026-08-26 08:25:00', 128, 86, 37.0, 83, 'WARN', 17),
('2026-08-25 09:38:00', 137, 90, 36.9, 62, 'HIGH', 17),
('2026-08-24 09:45:00', 126, 95, 36.9, 69, 'HIGH', 17),
('2026-09-01 14:59:00', 144, 85, 36.4, 79, 'HIGH', 18),
('2026-08-31 15:45:00', 141, 84, 36.9, 61, 'HIGH', 18),
('2026-08-31 14:21:00', 148, 72, 37.6, 97, 'HIGH', 18),
('2026-08-28 08:38:00', 135, 81, 37.6, 97, 'HIGH', 18),
('2026-08-28 09:46:00', 108, 95, 36.4, 84, 'HIGH', 18),
('2026-08-27 09:02:00', 141, 72, 36.5, 88, 'HIGH', 18),
('2026-08-26 15:09:00', 131, 78, 36.8, 90, 'HIGH', 18),
('2026-08-26 08:45:00', 113, 78, 37.0, 90, 'HIGH', 18),
('2026-08-25 14:11:00', 134, 86, 37.0, 76, 'WARN', 18),
('2026-08-25 15:12:00', 120, 82, 37.0, 74, 'WARN', 18),
('2026-08-24 14:45:00', 118, 96, 36.6, 82, 'HIGH', 18),
('2026-08-24 14:18:00', 112, 89, 37.5, 85, 'WARN', 18),
('2026-09-01 14:02:00', 123, 70, 36.7, 88, 'WARN', 19);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-31 15:13:00', 117, 82, 37.5, 77, 'WARN', 19),
('2026-08-31 09:06:00', 144, 80, 36.5, 93, 'HIGH', 19),
('2026-08-28 09:03:00', 111, 91, 36.7, 90, 'HIGH', 19),
('2026-08-27 09:00:00', 140, 75, 36.8, 90, 'HIGH', 19),
('2026-08-26 09:48:00', 123, 85, 36.6, 63, 'WARN', 19),
('2026-08-26 08:41:00', 141, 79, 36.9, 62, 'HIGH', 19),
('2026-08-25 15:56:00', 116, 67, 37.6, 85, 'WARN', 19),
('2026-09-01 14:56:00', 107, 65, 36.6, 98, 'HIGH', 20),
('2026-08-31 14:18:00', 134, 96, 37.4, 68, 'HIGH', 20),
('2026-08-28 14:12:00', 112, 86, 36.4, 89, 'WARN', 20),
('2026-08-28 14:45:00', 116, 65, 37.2, 78, 'NORMAL', 20),
('2026-08-27 09:39:00', 145, 90, 37.4, 92, 'HIGH', 20),
('2026-08-26 08:25:00', 147, 71, 36.5, 68, 'HIGH', 20),
('2026-08-26 15:20:00', 120, 65, 36.6, 75, 'NORMAL', 20),
('2026-08-25 14:21:00', 124, 65, 36.6, 83, 'WARN', 20),
('2026-08-25 09:03:00', 147, 72, 36.9, 70, 'HIGH', 20),
('2026-08-24 14:44:00', 112, 83, 36.7, 74, 'WARN', 20),
('2026-08-24 09:08:00', 135, 74, 36.8, 98, 'HIGH', 20),
('2026-09-01 15:48:00', 139, 78, 37.3, 98, 'HIGH', 21),
('2026-09-01 08:33:00', 133, 88, 36.3, 96, 'HIGH', 21);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-31 08:53:00', 140, 77, 37.0, 69, 'HIGH', 21),
('2026-08-28 14:54:00', 138, 93, 36.4, 73, 'HIGH', 21),
('2026-08-27 08:57:00', 137, 93, 37.3, 89, 'HIGH', 21),
('2026-08-27 09:32:00', 131, 94, 37.0, 95, 'HIGH', 21),
('2026-08-26 14:46:00', 106, 90, 36.6, 60, 'HIGH', 21),
('2026-08-26 09:37:00', 109, 67, 36.8, 64, 'NORMAL', 21),
('2026-08-25 08:59:00', 135, 67, 36.6, 71, 'WARN', 21),
('2026-09-01 14:57:00', 129, 93, 37.4, 84, 'HIGH', 22),
('2026-09-01 15:05:00', 148, 73, 37.1, 82, 'HIGH', 22),
('2026-08-31 09:34:00', 130, 73, 37.2, 74, 'WARN', 22),
('2026-08-28 08:19:00', 134, 92, 36.9, 74, 'HIGH', 22),
('2026-08-27 15:22:00', 114, 82, 36.5, 67, 'WARN', 22),
('2026-08-26 15:39:00', 106, 80, 36.5, 66, 'WARN', 22),
('2026-09-01 15:38:00', 148, 68, 36.5, 62, 'HIGH', 23),
('2026-08-31 15:14:00', 139, 78, 37.3, 63, 'WARN', 23),
('2026-08-31 09:32:00', 123, 79, 37.3, 96, 'HIGH', 23),
('2026-08-28 14:15:00', 124, 74, 37.6, 93, 'HIGH', 23),
('2026-08-28 09:26:00', 124, 82, 36.3, 97, 'HIGH', 23),
('2026-08-27 15:35:00', 136, 68, 37.6, 84, 'WARN', 23),
('2026-08-26 15:26:00', 114, 84, 36.7, 94, 'HIGH', 23);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-26 15:15:00', 119, 84, 37.4, 69, 'WARN', 23),
('2026-08-25 08:35:00', 131, 91, 37.0, 68, 'HIGH', 23),
('2026-08-25 15:15:00', 121, 78, 36.7, 65, 'NORMAL', 23),
('2026-08-24 14:05:00', 139, 77, 36.3, 84, 'WARN', 23),
('2026-08-24 08:55:00', 109, 77, 37.3, 97, 'HIGH', 23),
('2026-09-01 15:13:00', 126, 84, 37.5, 60, 'WARN', 24),
('2026-08-31 09:47:00', 112, 95, 37.5, 98, 'HIGH', 24),
('2026-08-28 15:58:00', 120, 85, 37.3, 84, 'WARN', 24),
('2026-08-27 14:19:00', 121, 88, 36.9, 91, 'HIGH', 24),
('2026-08-27 15:06:00', 135, 85, 37.5, 83, 'WARN', 24),
('2026-08-26 15:02:00', 141, 79, 37.2, 61, 'HIGH', 24),
('2026-08-26 14:35:00', 142, 91, 36.6, 72, 'HIGH', 24),
('2026-08-25 09:24:00', 141, 80, 36.9, 81, 'HIGH', 24),
('2026-08-25 14:48:00', 136, 96, 36.8, 82, 'HIGH', 24),
('2026-08-24 09:46:00', 139, 96, 36.5, 94, 'HIGH', 24),
('2026-09-01 08:52:00', 147, 68, 37.3, 86, 'HIGH', 25),
('2026-08-31 09:04:00', 150, 74, 36.2, 92, 'HIGH', 25),
('2026-08-28 14:03:00', 144, 95, 37.1, 61, 'HIGH', 25),
('2026-08-28 08:34:00', 140, 91, 36.2, 93, 'HIGH', 25),
('2026-08-27 14:01:00', 137, 92, 37.3, 71, 'HIGH', 25);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-27 08:58:00', 111, 74, 36.5, 93, 'HIGH', 25),
('2026-09-01 14:50:00', 130, 70, 36.7, 85, 'WARN', 26),
('2026-09-01 15:36:00', 120, 79, 36.6, 65, 'NORMAL', 26),
('2026-08-31 08:25:00', 129, 89, 37.0, 63, 'WARN', 26),
('2026-08-28 09:05:00', 136, 92, 37.1, 81, 'HIGH', 26),
('2026-08-27 08:14:00', 118, 95, 36.6, 64, 'HIGH', 26),
('2026-08-26 08:11:00', 125, 66, 37.5, 97, 'HIGH', 26),
('2026-08-26 09:48:00', 150, 90, 37.5, 79, 'HIGH', 26),
('2026-08-25 09:36:00', 129, 86, 36.7, 68, 'WARN', 26),
('2026-09-01 08:06:00', 132, 79, 37.4, 81, 'WARN', 27),
('2026-09-01 15:49:00', 125, 66, 37.1, 88, 'WARN', 27),
('2026-08-31 09:22:00', 140, 89, 36.8, 97, 'HIGH', 27),
('2026-08-31 15:05:00', 144, 83, 37.3, 64, 'HIGH', 27),
('2026-08-28 14:09:00', 129, 74, 37.2, 80, 'WARN', 27),
('2026-08-27 08:05:00', 105, 84, 36.8, 77, 'WARN', 27),
('2026-08-27 08:08:00', 110, 76, 36.8, 95, 'HIGH', 27),
('2026-09-01 08:01:00', 110, 87, 37.0, 98, 'HIGH', 28),
('2026-09-01 14:55:00', 129, 65, 36.6, 84, 'WARN', 28),
('2026-08-31 09:36:00', 138, 75, 37.2, 70, 'WARN', 28),
('2026-08-28 14:19:00', 122, 96, 36.4, 70, 'HIGH', 28);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-27 14:26:00', 124, 95, 37.3, 83, 'HIGH', 28),
('2026-08-27 14:15:00', 145, 96, 37.0, 72, 'HIGH', 28),
('2026-08-26 08:08:00', 124, 65, 37.5, 81, 'WARN', 28),
('2026-08-26 15:51:00', 126, 93, 36.7, 98, 'HIGH', 28),
('2026-08-25 14:20:00', 143, 77, 36.9, 71, 'HIGH', 28),
('2026-08-24 14:18:00', 149, 96, 37.6, 75, 'HIGH', 28),
('2026-08-24 14:24:00', 122, 90, 36.7, 96, 'HIGH', 28),
('2026-09-01 08:46:00', 134, 78, 36.8, 78, 'WARN', 29),
('2026-08-31 15:43:00', 136, 73, 37.1, 75, 'WARN', 29),
('2026-08-28 09:45:00', 132, 89, 36.3, 98, 'HIGH', 29),
('2026-08-28 15:37:00', 130, 91, 37.0, 83, 'HIGH', 29),
('2026-08-27 08:49:00', 120, 87, 36.4, 62, 'WARN', 29),
('2026-08-26 14:50:00', 132, 71, 36.2, 76, 'WARN', 29),
('2026-08-26 09:32:00', 138, 79, 36.8, 85, 'WARN', 29),
('2026-09-01 14:01:00', 135, 71, 36.6, 65, 'WARN', 30),
('2026-08-31 09:22:00', 124, 86, 36.8, 73, 'WARN', 30),
('2026-08-28 14:30:00', 111, 93, 37.2, 88, 'HIGH', 30),
('2026-08-28 14:04:00', 124, 67, 37.3, 67, 'NORMAL', 30),
('2026-08-27 14:41:00', 111, 75, 37.5, 75, 'NORMAL', 30),
('2026-08-26 09:21:00', 140, 92, 37.5, 74, 'HIGH', 30);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-25 09:11:00', 145, 92, 36.8, 72, 'HIGH', 30),
('2026-08-25 15:37:00', 132, 89, 36.2, 73, 'WARN', 30),
('2026-08-24 14:48:00', 150, 69, 37.0, 94, 'HIGH', 30),
('2026-09-01 14:12:00', 134, 72, 36.6, 91, 'HIGH', 31),
('2026-09-01 14:38:00', 129, 90, 37.0, 82, 'HIGH', 31),
('2026-08-31 15:39:00', 116, 84, 37.5, 97, 'HIGH', 31),
('2026-08-31 08:43:00', 113, 85, 36.4, 79, 'WARN', 31),
('2026-08-28 09:23:00', 149, 74, 36.9, 86, 'HIGH', 31),
('2026-08-27 15:27:00', 116, 96, 37.1, 71, 'HIGH', 31),
('2026-08-26 15:18:00', 113, 76, 36.6, 88, 'WARN', 31),
('2026-09-01 14:00:00', 136, 73, 36.5, 84, 'WARN', 32),
('2026-08-31 15:43:00', 136, 91, 37.2, 88, 'HIGH', 32),
('2026-08-31 15:10:00', 110, 66, 37.3, 74, 'NORMAL', 32),
('2026-08-28 08:17:00', 119, 83, 36.4, 89, 'WARN', 32),
('2026-08-28 15:35:00', 137, 72, 37.0, 77, 'WARN', 32),
('2026-08-27 08:48:00', 133, 78, 37.6, 66, 'WARN', 32),
('2026-08-27 09:19:00', 107, 93, 36.6, 65, 'HIGH', 32),
('2026-08-26 08:49:00', 120, 78, 37.3, 97, 'HIGH', 32),
('2026-08-26 14:55:00', 150, 92, 36.4, 68, 'HIGH', 32),
('2026-08-25 09:51:00', 108, 87, 36.9, 98, 'HIGH', 32);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-24 14:45:00', 123, 83, 37.0, 92, 'HIGH', 32),
('2026-09-01 15:58:00', 108, 82, 37.4, 68, 'WARN', 33),
('2026-08-31 09:09:00', 150, 85, 37.4, 85, 'HIGH', 33),
('2026-08-28 09:36:00', 145, 82, 37.1, 84, 'HIGH', 33),
('2026-08-28 15:04:00', 145, 70, 36.8, 77, 'HIGH', 33),
('2026-08-27 15:31:00', 125, 65, 37.4, 65, 'NORMAL', 33),
('2026-08-27 15:40:00', 147, 87, 37.4, 94, 'HIGH', 33),
('2026-09-01 15:52:00', 118, 96, 36.6, 78, 'HIGH', 34),
('2026-08-31 09:36:00', 136, 86, 37.5, 63, 'WARN', 34),
('2026-08-31 08:06:00', 145, 94, 36.2, 70, 'HIGH', 34),
('2026-08-28 15:00:00', 132, 77, 37.6, 68, 'WARN', 34),
('2026-08-28 14:10:00', 122, 70, 37.1, 76, 'NORMAL', 34),
('2026-08-27 14:42:00', 146, 75, 36.3, 79, 'HIGH', 34),
('2026-08-26 15:41:00', 110, 71, 36.2, 90, 'HIGH', 34),
('2026-08-25 09:37:00', 119, 93, 36.2, 81, 'HIGH', 34),
('2026-08-24 15:44:00', 113, 95, 36.3, 84, 'HIGH', 34),
('2026-09-01 08:20:00', 128, 84, 36.4, 68, 'WARN', 35),
('2026-08-31 08:33:00', 141, 65, 37.1, 70, 'HIGH', 35),
('2026-08-28 14:46:00', 118, 74, 37.5, 88, 'WARN', 35),
('2026-08-28 09:05:00', 111, 73, 37.3, 97, 'HIGH', 35);
INSERT IGNORE INTO biometrics (measured_at, systolic_bp, diastolic_bp, temperature, heart_rate, risk_level, employee_id)
VALUES
('2026-08-27 14:27:00', 125, 73, 36.5, 65, 'NORMAL', 35),
('2026-08-27 09:35:00', 143, 83, 37.3, 61, 'HIGH', 35);

-- ------------------------------
-- 7-1. checkups 추가 (2024~2026년, 직원 1~35)
-- ------------------------------
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2024, '2024-11-10', '간 수치 약간 상승, 재검 권고', '2024-11-11 09:00:00', 1),
(2025, '2025-09-07', '혈당 경계 수치, 추적 관찰 필요', '2025-09-08 09:00:00', 1),
(2026, '2026-01-26', '간 수치 약간 상승, 재검 권고', '2026-01-27 09:00:00', 1),
(2024, '2024-02-08', '요통 관련 소견, 자세 교정 권고', '2024-02-09 09:00:00', 2),
(2025, '2025-02-17', '혈압 정상, 특이 소견 없음', '2025-02-18 09:00:00', 2),
(2026, '2026-06-05', '혈당 경계 수치, 추적 관찰 필요', '2026-06-06 09:00:00', 2),
(2024, '2024-10-14', '전반적 양호, 다음 검진 시기 안내', '2024-10-15 09:00:00', 3),
(2025, '2025-03-25', '요통 관련 소견, 자세 교정 권고', '2025-03-26 09:00:00', 3),
(2026, '2026-02-01', '콜레스테롤 수치 경계, 식습관 개선 권고', '2026-02-02 09:00:00', 3),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 4),
(2025, '2025-01-26', '혈당 경계 수치, 추적 관찰 필요', '2025-01-27 09:00:00', 4),
(2026, '2026-11-23', '혈당 경계 수치, 추적 관찰 필요', '2026-11-24 09:00:00', 4),
(2024, '2024-07-03', '체중 증가 추세, 운동 권장', '2024-07-04 09:00:00', 5),
(2025, '2025-05-03', '간 수치 약간 상승, 재검 권고', '2025-05-04 09:00:00', 5),
(2026, '2026-03-18', '전반적 양호, 다음 검진 시기 안내', '2026-03-19 09:00:00', 5),
(2024, '2024-11-25', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-11-26 09:00:00', 6),
(2025, '2025-07-24', '간 수치 약간 상승, 재검 권고', '2025-07-25 09:00:00', 6),
(2026, NULL, '검진 미실시 상태', '2026-08-10 11:00:00', 6),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 7),
(2025, '2025-08-20', '혈압 정상, 특이 소견 없음', '2025-08-21 09:00:00', 7);
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2026, '2026-11-24', '체중 증가 추세, 운동 권장', '2026-11-25 09:00:00', 7),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 8),
(2025, '2025-01-06', '요통 관련 소견, 자세 교정 권고', '2025-01-07 09:00:00', 8),
(2026, '2026-09-17', '시력 저하 소견, 안과 진료 권장', '2026-09-18 09:00:00', 8),
(2024, '2024-03-24', '시력 저하 소견, 안과 진료 권장', '2024-03-25 09:00:00', 9),
(2025, '2025-02-25', '혈압 정상, 특이 소견 없음', '2025-02-26 09:00:00', 9),
(2026, '2026-07-09', '콜레스테롤 수치 경계, 식습관 개선 권고', '2026-07-10 09:00:00', 9),
(2024, '2024-10-21', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-10-22 09:00:00', 10),
(2025, '2025-09-12', '혈압 정상, 특이 소견 없음', '2025-09-13 09:00:00', 10),
(2026, '2026-10-06', '전반적 양호, 다음 검진 시기 안내', '2026-10-07 09:00:00', 10),
(2024, '2024-02-19', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-02-20 09:00:00', 11),
(2025, '2025-09-01', '혈압 정상, 특이 소견 없음', '2025-09-02 09:00:00', 11),
(2026, '2026-01-08', '혈압 정상, 특이 소견 없음', '2026-01-09 09:00:00', 11),
(2024, '2024-07-05', '체중 증가 추세, 운동 권장', '2024-07-06 09:00:00', 12),
(2025, '2025-01-18', '혈당 경계 수치, 추적 관찰 필요', '2025-01-19 09:00:00', 12),
(2026, '2026-04-14', '전반적 양호, 다음 검진 시기 안내', '2026-04-15 09:00:00', 12),
(2024, '2024-02-19', '전반적 양호, 다음 검진 시기 안내', '2024-02-20 09:00:00', 13),
(2025, NULL, '검진 미실시 상태', '2025-08-10 11:00:00', 13),
(2026, '2026-01-06', '간 수치 약간 상승, 재검 권고', '2026-01-07 09:00:00', 13),
(2024, '2024-01-13', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-01-14 09:00:00', 14);
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2025, '2025-05-25', '시력 저하 소견, 안과 진료 권장', '2025-05-26 09:00:00', 14),
(2026, '2026-09-15', '혈압 정상, 특이 소견 없음', '2026-09-16 09:00:00', 14),
(2024, '2024-05-19', '시력 저하 소견, 안과 진료 권장', '2024-05-20 09:00:00', 16),
(2025, '2025-04-15', '전반적 양호, 다음 검진 시기 안내', '2025-04-16 09:00:00', 16),
(2026, '2026-08-25', '간 수치 약간 상승, 재검 권고', '2026-08-26 09:00:00', 16),
(2024, '2024-09-25', '간 수치 약간 상승, 재검 권고', '2024-09-26 09:00:00', 17),
(2025, '2025-07-01', '간 수치 약간 상승, 재검 권고', '2025-07-02 09:00:00', 17),
(2026, '2026-11-27', '혈압 정상, 특이 소견 없음', '2026-11-28 09:00:00', 17),
(2024, '2024-01-25', '혈당 경계 수치, 추적 관찰 필요', '2024-01-26 09:00:00', 18),
(2025, '2025-05-04', '간 수치 약간 상승, 재검 권고', '2025-05-05 09:00:00', 18),
(2026, '2026-04-14', '요통 관련 소견, 자세 교정 권고', '2026-04-15 09:00:00', 18),
(2024, '2024-03-23', '시력 저하 소견, 안과 진료 권장', '2024-03-24 09:00:00', 19),
(2025, NULL, '검진 미실시 상태', '2025-08-10 11:00:00', 19),
(2026, '2026-09-14', '전반적 양호, 다음 검진 시기 안내', '2026-09-15 09:00:00', 19),
(2024, '2024-02-19', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-02-20 09:00:00', 20),
(2025, '2025-11-27', '혈당 경계 수치, 추적 관찰 필요', '2025-11-28 09:00:00', 20),
(2026, NULL, '검진 미실시 상태', '2026-08-10 11:00:00', 20),
(2024, '2024-07-22', '체중 증가 추세, 운동 권장', '2024-07-23 09:00:00', 21),
(2025, '2025-05-28', '전반적 양호, 다음 검진 시기 안내', '2025-05-28 09:00:00', 21),
(2026, '2026-07-23', '전반적 양호, 다음 검진 시기 안내', '2026-07-24 09:00:00', 21);
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2024, '2024-05-08', '콜레스테롤 수치 경계, 식습관 개선 권고', '2024-05-09 09:00:00', 22),
(2025, '2025-03-25', '콜레스테롤 수치 경계, 식습관 개선 권고', '2025-03-26 09:00:00', 22),
(2026, '2026-03-15', '요통 관련 소견, 자세 교정 권고', '2026-03-16 09:00:00', 22),
(2024, '2024-02-18', '전반적 양호, 다음 검진 시기 안내', '2024-02-19 09:00:00', 23),
(2025, '2025-04-15', '시력 저하 소견, 안과 진료 권장', '2025-04-16 09:00:00', 23),
(2026, '2026-02-03', '체중 증가 추세, 운동 권장', '2026-02-04 09:00:00', 23),
(2024, '2024-05-27', '혈압 정상, 특이 소견 없음', '2024-05-28 09:00:00', 24),
(2025, '2025-06-05', '콜레스테롤 수치 경계, 식습관 개선 권고', '2025-06-06 09:00:00', 24),
(2026, '2026-06-13', '혈압 정상, 특이 소견 없음', '2026-06-14 09:00:00', 24),
(2024, '2024-05-27', '체중 증가 추세, 운동 권장', '2024-05-28 09:00:00', 25),
(2025, NULL, '검진 미실시 상태', '2025-08-10 11:00:00', 25),
(2026, '2026-08-18', '간 수치 약간 상승, 재검 권고', '2026-08-19 09:00:00', 25),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 26),
(2025, NULL, '검진 미실시 상태', '2025-08-10 11:00:00', 26),
(2026, '2026-02-01', '혈압 정상, 특이 소견 없음', '2026-02-02 09:00:00', 26),
(2024, '2024-04-05', '간 수치 약간 상승, 재검 권고', '2024-04-06 09:00:00', 27),
(2025, '2025-06-22', '콜레스테롤 수치 경계, 식습관 개선 권고', '2025-06-23 09:00:00', 27),
(2026, '2026-05-27', '콜레스테롤 수치 경계, 식습관 개선 권고', '2026-05-28 09:00:00', 27),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 28),
(2025, NULL, '검진 미실시 상태', '2025-08-10 11:00:00', 28);
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2026, '2026-08-26', '체중 증가 추세, 운동 권장', '2026-08-27 09:00:00', 28),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 29),
(2025, '2025-06-04', '혈압 정상, 특이 소견 없음', '2025-06-05 09:00:00', 29),
(2026, '2026-01-06', '혈당 경계 수치, 추적 관찰 필요', '2026-01-07 09:00:00', 29),
(2024, '2024-07-16', '혈압 정상, 특이 소견 없음', '2024-07-17 09:00:00', 30),
(2025, '2025-07-06', '전반적 양호, 다음 검진 시기 안내', '2025-07-07 09:00:00', 30),
(2026, '2026-03-09', '시력 저하 소견, 안과 진료 권장', '2026-03-10 09:00:00', 30),
(2024, '2024-03-02', '간 수치 약간 상승, 재검 권고', '2024-03-03 09:00:00', 31),
(2025, '2025-08-14', '요통 관련 소견, 자세 교정 권고', '2025-08-15 09:00:00', 31),
(2026, NULL, '검진 미실시 상태', '2026-08-10 11:00:00', 31),
(2024, '2024-03-14', '간 수치 약간 상승, 재검 권고', '2024-03-15 09:00:00', 32),
(2025, '2025-09-08', '혈압 정상, 특이 소견 없음', '2025-09-09 09:00:00', 32),
(2026, '2026-06-16', '요통 관련 소견, 자세 교정 권고', '2026-06-17 09:00:00', 32),
(2024, '2024-10-17', '전반적 양호, 다음 검진 시기 안내', '2024-10-18 09:00:00', 33),
(2025, '2025-03-01', '전반적 양호, 다음 검진 시기 안내', '2025-03-02 09:00:00', 33),
(2026, '2026-01-26', '시력 저하 소견, 안과 진료 권장', '2026-01-27 09:00:00', 33),
(2024, NULL, '검진 미실시 상태', '2024-08-10 11:00:00', 34),
(2025, '2025-09-12', '간 수치 약간 상승, 재검 권고', '2025-09-13 09:00:00', 34),
(2026, '2026-02-08', '간 수치 약간 상승, 재검 권고', '2026-02-09 09:00:00', 34),
(2024, '2024-01-25', '간 수치 약간 상승, 재검 권고', '2024-01-26 09:00:00', 35);
INSERT IGNORE INTO checkups (checkup_year, checkup_date, checkup_summary, checkup_created_at, employee_id)
VALUES
(2025, '2025-07-12', '체중 증가 추세, 운동 권장', '2025-07-13 09:00:00', 35),
(2026, '2026-10-11', '전반적 양호, 다음 검진 시기 안내', '2026-10-12 09:00:00', 35);

-- ------------------------------
-- 10-1. consultations 추가
-- ------------------------------
INSERT IGNORE INTO consultations (employee_id, manager_id, scheduled_date, scheduled_turn, reason, content, status, consultated_at, created_at)
VALUES
(2, 3, '2026-09-17', 'T2', '허리 통증 상담', '장시간 근무로 인한 허리 통증 관리 방법을 알고 싶습니다.', 'RESERVED', NULL, '2026-08-16 00:00:00'),
(21, 2, '2026-09-12', 'T1', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'RESERVED', NULL, '2026-08-24 00:00:00'),
(16, 2, '2026-09-14', 'T4', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'FINISHED', '2026-09-14 16:00:00', '2026-08-25 00:00:00'),
(9, 3, '2026-08-23', 'T3', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'RESERVED', NULL, '2026-08-27 00:00:00'),
(10, 2, '2026-09-14', 'T3', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'FINISHED', '2026-09-14 16:00:00', '2026-08-10 00:00:00'),
(27, 2, '2026-09-02', 'T3', '체중 관리 상담', '체중 증가로 인한 건강 관리 방안을 상담하고 싶습니다.', 'RESERVED', NULL, '2026-08-26 00:00:00'),
(29, 3, '2026-09-01', 'T2', '수면 장애 상담', '불규칙한 수면 패턴으로 인한 피로 누적 상담입니다.', 'CANCELED', NULL, '2026-08-09 00:00:00'),
(24, 2, '2026-09-18', 'T2', '금연 상담', '금연 프로그램 참여 관련 상담을 요청합니다.', 'FINISHED', '2026-09-18 16:00:00', '2026-08-09 00:00:00'),
(20, 2, '2026-08-23', 'T1', '수면 장애 상담', '불규칙한 수면 패턴으로 인한 피로 누적 상담입니다.', 'CANCELED', NULL, '2026-08-19 00:00:00'),
(3, 2, '2026-09-19', 'T3', '심리 상담', '업무 관련 정서적 어려움에 대한 상담을 원합니다.', 'CANCELED', NULL, '2026-08-24 00:00:00'),
(7, 3, '2026-08-21', 'T3', '수면 장애 상담', '불규칙한 수면 패턴으로 인한 피로 누적 상담입니다.', 'RESERVED', NULL, '2026-08-24 00:00:00'),
(24, 3, '2026-08-22', 'T1', '심리 상담', '업무 관련 정서적 어려움에 대한 상담을 원합니다.', 'CANCELED', NULL, '2026-08-10 00:00:00'),
(23, 3, '2026-08-12', 'T3', '혈압 관리 상담', '최근 혈압이 높아져 생활습관 점검이 필요합니다.', 'RESERVED', NULL, '2026-08-09 00:00:00'),
(5, 3, '2026-08-28', 'T1', '수면 장애 상담', '불규칙한 수면 패턴으로 인한 피로 누적 상담입니다.', 'RESERVED', NULL, '2026-08-30 00:00:00'),
(26, 3, '2026-09-07', 'T2', '금연 상담', '금연 프로그램 참여 관련 상담을 요청합니다.', 'FINISHED', '2026-09-07 15:00:00', '2026-08-20 00:00:00'),
(26, 2, '2026-09-11', 'T2', '혈압 관리 상담', '최근 혈압이 높아져 생활습관 점검이 필요합니다.', 'FINISHED', '2026-09-11 14:00:00', '2026-08-30 00:00:00'),
(20, 2, '2026-09-01', 'T3', '스트레스 관리 상담', '업무 스트레스와 수면 패턴에 대한 상담을 원합니다.', 'RESERVED', NULL, '2026-08-21 00:00:00'),
(11, 3, '2026-08-22', 'T1', '허리 통증 상담', '장시간 근무로 인한 허리 통증 관리 방법을 알고 싶습니다.', 'RESERVED', NULL, '2026-08-21 00:00:00'),
(32, 2, '2026-09-08', 'T2', '허리 통증 상담', '장시간 근무로 인한 허리 통증 관리 방법을 알고 싶습니다.', 'FINISHED', '2026-09-08 14:00:00', '2026-08-30 00:00:00'),
(7, 3, '2026-08-25', 'T2', '금연 상담', '금연 프로그램 참여 관련 상담을 요청합니다.', 'CANCELED', NULL, '2026-08-14 00:00:00');

-- ------------------------------
-- 11-1. notices 추가 (페이지네이션 테스트용)
-- ------------------------------
INSERT IGNORE INTO notices (title, content, status, created_at, update_at, author_id, count)
VALUES
('10월 정기 건강검진 일정 안내', '10월 정기 건강검진 예약 일정과 대상자를 안내드립니다.', 'Y', '2026-09-01 00:00:00', '2026-09-01 00:00:00', 3, 51),
('독감 예방접종 신청 안내', '겨울철 독감 예방접종을 희망하는 직원은 신청해 주세요.', 'Y', '2026-08-29 00:00:00', '2026-08-29 00:00:00', 3, 21),
('사내 헬스장 이용 안내', '사내 헬스장 이용 시간 및 유의사항을 안내드립니다.', 'Y', '2026-08-26 00:00:00', '2026-08-26 00:00:00', 3, 23),
('금연 클리닉 운영 안내', '금연을 희망하는 직원을 위한 클리닉을 운영합니다.', 'Y', '2026-08-23 00:00:00', '2026-08-23 00:00:00', 2, 20),
('스트레스 관리 프로그램 안내', '심리 상담 및 스트레스 관리 프로그램을 안내드립니다.', 'Y', '2026-08-20 00:00:00', '2026-08-20 00:00:00', 1, 8),
('여름철 건강 관리 수칙 안내', '무더위 속 건강관리를 위한 수칙을 안내드립니다.', 'Y', '2026-08-17 00:00:00', '2026-08-17 00:00:00', 2, 41),
('사내 식당 메뉴 개편 안내', '영양 균형을 고려한 사내 식당 메뉴 개편 안내입니다.', 'Y', '2026-08-14 00:00:00', '2026-08-14 00:00:00', 3, 58),
('정기 소방/안전 교육 일정 안내', '전 직원 대상 소방 및 안전 교육 일정을 안내드립니다.', 'Y', '2026-08-11 00:00:00', '2026-08-11 00:00:00', 2, 14),
('명절 연휴 근무 안내', '다가오는 명절 연휴 기간 근무 및 당직 안내입니다.', 'Y', '2026-08-08 00:00:00', '2026-08-08 00:00:00', 1, 28),
('건강검진 결과 상담 신청 안내', '건강검진 결과에 대한 1:1 상담 신청 방법을 안내드립니다.', 'Y', '2026-08-05 00:00:00', '2026-08-05 00:00:00', 1, 32),
('사내 금연구역 확대 안내', '사내 금연구역이 확대 운영됨을 안내드립니다.', 'Y', '2026-08-02 00:00:00', '2026-08-02 00:00:00', 1, 23),
('신규 입사자 건강검진 안내', '신규 입사자 대상 필수 건강검진 절차를 안내드립니다.', 'Y', '2026-07-30 00:00:00', '2026-07-30 00:00:00', 3, 45);