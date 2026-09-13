# 안전 브리핑 수동 실험

`src/test/java/com/kh/healthgate/support/SafetyBriefingExperiment.java`의 `main()`을 IDE에서 실행하거나 아래 Maven 명령을 사용합니다. 일반 `mvn test`에서는 실행되지 않습니다. 실제 Google 모델 호출 비용이 발생합니다.

## 준비

- 작업 디렉터리: `healthgate_be`
- 프로젝트의 환경변수를 실행 환경에 로드합니다. `.env` 파일을 이 실행 클래스가 자동으로 읽지는 않습니다. IDE의 환경변수 설정 또는 기존 direnv 환경을 사용하세요.
- API 키: `SPRING_AI_GOOGLE_GENAI_APIKEY` (또는 `spring.ai.google.genai.api-key` 시스템 속성)
- 모델: 기존 `SPRING_AI_GOOGLE_GENAI_CHAT_MODEL` / `application.properties` 설정
- 로컬 MySQL: `localhost:3306/healthgate_db`, 개발 계정 `root` / `mysql`
- 관리자 화면에서 사용할 문서를 활성화하고 현재 설정으로 인덱싱을 완료합니다.
- 벡터 파일: 기존 `com.kh.healthgate.ai.vector-store-file-path` 설정

파이프라인 버전은 `pdf-page-v3`입니다. 버전 변경 후에는 애플리케이션을 재시작하고 관리자 화면에서 문서를 다시 인덱싱하세요. 문서 제목을 수정한 경우에도 재인덱싱이 필요합니다. 현재 제목·파일명·파이프라인 설정에 맞는 인덱싱이 완료된 문서만 검색에 사용됩니다.

## 실행

```sh
mvn -q test-compile exec:java@briefing-experiment -Dexec.args=normal
mvn -q test-compile exec:java@briefing-experiment -Dexec.args=hot
mvn -q test-compile exec:java@briefing-experiment -Dexec.args=empty
mvn -q test-compile exec:java@briefing-experiment -Dexec.args=all
```

- `normal`: 합성 예보 24℃·습도 50%, 실제 활성·인덱싱 완료 문서
- `hot`: 합성 예보 35℃·습도 60%, 실제 활성·인덱싱 완료 문서
- `empty`: 평범한 합성 예보, 문서 목록을 비운 상태. 실제 문서를 비활성화하지 않으며 DB·벡터 파일 없이 실행 가능
- `all`: 위 세 경우를 차례로 실행

기본 예보의 날짜는 2026-09-14, 근무시간은 09~18시입니다. 원하는 기상 컨텍스트를 UTF-8 파일에 작성해 사용할 수 있습니다.

```sh
mvn -q test-compile exec:java@briefing-experiment -Dexec.args=hot -Dbriefing.weather-file=/absolute/path/weather.txt
```

사용자 입력 파일은 선택한 모든 시나리오의 예보를 대체합니다. API 키를 명령행 인자나 결과 파일에 넣지 마세요.

## 결과와 검토

`logs/experiments/<실행시각>/<시나리오>.md`에 모델명, 예보, 문서 목록, 검색어, 검색된 청크의 ID·점수·메타데이터·본문, 최종 응답을 저장합니다. 생성 실패 시에도 그 전에 완료된 검색 결과는 남습니다.

확인할 항목:

- 기상 위험이 있을 때 관련 문서의 내용이 검색되고 우선 안내되는가?
- 평범한 날에 적용 가능한 작업 수칙과 상식적인 주의사항이 자연스럽게 제공되는가?
- 특정 작업의 적용 상황이 명확하며, 모든 작업자가 그 일을 한다고 가정하지 않는가?
- 문서의 조건·수치·의미를 보존하고 중복 없이 충분히 활용하는가?
- 문서가 없을 때 전문적인 수칙이나 수치를 만들지 않는가?

모델 출력은 실행마다 달라질 수 있으므로 결과 파일을 비교해 검토하세요.

## 생성 프롬프트 비교

`SafetyBriefingPromptComparison.main()`에 실험 디렉터리 경로를 인자로 전달하면 `normal.md`, `hot.md`의 예보와 검색 본문을 사용하여 현재 프롬프트와 후보 프롬프트를 비교합니다. 각 조건·프롬프트를 2회씩 총 8회 호출하며, 반복마다 호출 순서를 바꿉니다.

```sh
mvn -q test-compile exec:java -Dexec.classpathScope=test -Dexec.mainClass=com.kh.healthgate.safety.ai.briefing.SafetyBriefingPromptComparison '-Dexec.args=logs/experiments/<실행시각>'
```

`logs/experiments/comparison-<실행시각>/`에 두 프롬프트 원문, 실제 생성 입력, 각 응답을 저장합니다.
