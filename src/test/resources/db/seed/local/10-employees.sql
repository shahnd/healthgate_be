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

