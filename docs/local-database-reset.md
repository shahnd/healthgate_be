# 로컬 데이터베이스 초기화

로컬 DB가 꼬였거나 현재 Flyway 스키마와 개발 데이터로 다시 구성해야 할 때 사용합니다.

> 이 작업은 `localhost:3306`의 `healthgate_db`를 삭제합니다. 기존 데이터는 복구되지 않습니다.
> 운영 DB는 이 도구로 초기화하지 않습니다.
> 초기화를 위해 `spring.jpa.hibernate.ddl-auto`나 Flyway 설정을 변경하지 않습니다.

## 실행 전 확인

- 로컬 MySQL 서버가 실행 중이어야 합니다.
- MySQL은 `localhost:3306`에서 접속할 수 있어야 합니다.
- 로컬 계정은 `root`, 비밀번호는 `mysql`이어야 합니다.
- 프로젝트 루트인 `healthgate_be` 디렉터리에서 명령을 실행합니다.

프로젝트에 포함된 Maven Wrapper와 JDBC를 사용합니다.

## 실행

macOS/Linux:

```sh
./mvnw test-compile exec:java@reset-local-db
```

Windows PowerShell 또는 명령 프롬프트:

```bat
mvnw.cmd test-compile exec:java@reset-local-db
```

경고가 표시되면 계속 진행할 경우에만 대문자로 `RESET`을 입력합니다.

```text
WARNING: localhost의 healthgate_db 데이터가 모두 삭제됩니다.
계속하려면 RESET을 입력하세요: RESET
```

## 실행되는 작업

초기화 도구는 다음 작업을 순서대로 수행합니다.

1. `healthgate_db`를 삭제하고 다시 생성합니다.
2. Flyway migration을 적용합니다.
3. 적용된 Flyway 버전과 직원 데이터 개수를 출력합니다.

다음 메시지가 표시되면 초기화가 완료된 것입니다.

```text
Flyway version: 1
Employee count: 38
Local database reset completed.
```

## 실행에 실패하는 경우

- `Access denied`: 로컬 MySQL의 `root/mysql` 계정을 확인합니다.
- `Communications link failure`: MySQL이 실행 중인지와 `3306` 포트를 확인합니다.
