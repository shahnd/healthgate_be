-- ============================================
-- HealthGate 추가 더미데이터 (대량, 페이지네이션/필터 테스트용)
-- 앞선 번호의 로컬 시딩 파일 실행 이후에 실행하세요.
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
