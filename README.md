# HealthGate Backend

직원의 건강과 안전을 관리하는 **HealthGate**의 백엔드입니다.  
직원 관리부터 건강검진, 상담, 안전문서까지 다양한 건강관리 기능을 REST API로 제공합니다.

## 주요 기능

- **인증 및 직원 관리** — 로그인, 직원 정보 및 역할 관리
- **건강관리** — 출퇴근 및 생체 정보 관리
- **건강검진** — 검진 대상자·통계·알림 관리
- **상담 및 예약** — 보건 상담과 예약 관리
- **안전관리** — 안전 브리핑과 안전문서 관리
- **공지사항 및 병원 관리** — 공지사항, 첨부파일, 병원 정보 관리
- **외부 데이터 연동** — 날씨 및 공휴일 정보 제공

## 기술 스택

- Java 21
- Spring Boot
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL
- Flyway
- Spring AI / Google Gemini

## 프로젝트 구성

도메인별로 API, 서비스, 데이터 접근 계층을 구성한 Spring Boot 애플리케이션입니다.  
프론트엔드와 연동해 HealthGate의 주요 업무 기능과 데이터를 제공합니다.
