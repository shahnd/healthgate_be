-- ------------------------------
-- 4. hospitals
-- ------------------------------
INSERT IGNORE INTO hospitals (name, address, phone, url, description, is_general_exam_available, is_stomach_cancer_exam_available, is_colon_cancer_exam_available, is_liver_cancer_exam_available, is_lung_cancer_exam_available, status)
VALUES
('강남메디컬센터', '서울특별시 강남구 테헤란로 123', '02-555-1111', 'https://gangnam-medical.example', '종합검진 및 내과 전문 진료를 제공하는 의료기관입니다.', 1, 1, 1, 1, 1, 'Y'),
('서울건강검진병원', '서울특별시 서초구 반포대로 45', '02-555-2222', 'https://seoul-health.example', '일반검진, 위암, 대장암, 간암 검사를 수행합니다.', 1, 1, 1, 1, 1, 'Y'),
('부산의료원', '부산광역시 해운대구 해운대로 88', '051-555-3333', 'https://busan-med.example', '산업안전검진과 정기 건강검진을 전문으로 합니다.', 1, 1, 1, 0, 1, 'Y');

