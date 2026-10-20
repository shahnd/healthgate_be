# HealthGate Backend

직원 건강관리 시스템 HealthGate의 백엔드입니다. Spring Boot 기반 REST API를 제공하며, 직원·병원·공지사항·상담·건강검진·안전문서 등의 기능을 담당합니다.

## 기술 스택

- Java 21
- Spring Boot 4
- Maven Wrapper
- Spring Web MVC
- Spring Data JPA
- Spring Security
- MySQL
- Flyway
- Spring AI / Google Gemini

## 사전 준비

- JDK 21
- MySQL
- 외부 기능을 사용할 경우 필요한 API 및 메일 계정 정보

## 데이터베이스 준비

기본 설정은 로컬 MySQL의 `healthgate_db` 데이터베이스를 사용합니다.

```sql
CREATE DATABASE healthgate_db
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
