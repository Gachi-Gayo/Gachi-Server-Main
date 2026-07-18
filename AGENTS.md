# AGENTS.md

이 저장소에서 작업하는 모든 Codex 에이전트가 따라야 하는 공통 작업 규칙이다. 개인 대화 상태와 비밀값은 저장소에 두지 않는다.

## 작업 방식

- **계획 모드:** 사용자가 `/plan`, `계획`, `검토`, `설계`를 요청하면 `.agents/skills/plan`을 사용한다. 명시적 승인 전에는 코드·문서 파일을 수정하거나 Linear 이슈, 브랜치/Worktree, Slack 알림을 만들지 않는다.
- **즉시 구현 모드:** 사용자가 `바로 구현해줘`처럼 명시하면 최신 Figma 확인 후 Linear 이슈, 브랜치, Slack 시작 알림, 구현을 진행한다.
- 계획을 실행으로 전환하는 유일한 신호는 `계획 승인 및 구현 시작`이다. `.agents/skills/implement`를 사용한다.
- 제품 기능은 계획·구현 전 최신 [Figma Feature list](https://www.figma.com/design/OJfSNQEMxvWXIXUM33cc5c/26-%EA%B4%80%EA%B4%91%EB%8D%B0%EC%9D%B4%ED%84%B0-%ED%99%9C%EC%9A%A9-%EA%B3%B5%EB%AA%A8%EC%A0%84?node-id=109-52&m=dev)를 다시 확인한다.
- 작업 단위는 사용자 흐름이다. 프레임은 온보딩 `397:2848`, 홈보드 `340:3638`, 계획 `340:3579`, 여행 기록 `397:2849`, 마이페이지 `397:2990`이다.

## 브랜치·상태

- 브랜치는 최신 `dev`에서 `feat|fix|refactor|chore/jin-<번호>-<흐름>` 형식으로 만든다. Codex App의 `codex/...` 임시 브랜치는 코드 변경 전에 반드시 이름을 바꾼다.
- `dev`에서 코드 파일을 직접 수정하지 않는다.
- Linear 상태는 `Backlog → Todo → In Progress → In Review → Done`이다. PR 병합 뒤에만 Done으로 옮긴다. 수동 작업·결정 대기는 `Blocked` 라벨과 사유를 남긴다.
- 검증이 끝나면 `origin`의 `Gachi-Gayo/Gachi-Server-Main`으로 푸시하고 `dev` 대상 Draft PR을 만든다. 이 동작은 사전 승인되어 있으므로 대화로 재승인을 묻지 않는다. 도구의 승인 화면이 필요한 경우에만 즉시 호출한다.

## 구현·API 규칙

- Red → Green → Refactor 순서로 테스트, 최소 구현, 정리를 진행한다.
- REST API의 엔드포인트·DTO 필드·오류 계약을 추가/수정/삭제하면 Controller의 `@Operation`·`@ApiResponse`, DTO의 `@Schema`, `ApiResponse`·`ErrorCode`·`GachiException`, 테스트와 Swagger 설명을 같은 변경에서 갱신한다.
- 새로 추가·변경한 REST API는 로컬에서 Happy case 1개와 의미 있는 edge case를 실제 호출한다. PR에는 시나리오·요청값·기대 응답·실제 응답·판정을 기록한다.
- REST API 변경이 없더라도 PR의 API 테스트 섹션에 `미실시(REST API 변경 없음)`, 사유, 실행한 테스트를 기록한다.

## 언어·보고·PR

- 사용자에게 보이는 Linear 이슈, Slack 메시지, GitHub PR 제목과 본문은 모두 **한국어**로 작성한다. 코드 식별자와 고유 기술명은 예외다.
- PR 생성 시 반드시 `.github/PULL_REQUEST_TEMPLATE.md`의 모든 섹션을 채운다. `gh pr create` 등으로 본문을 직접 지정할 때에도 템플릿을 생략하거나 영어 `Summary`/`Validation` 형식으로 대체하지 않는다.
- PR URL은 GitHub가 반환한 정식 URL이 실제로 열리는 것을 확인한 뒤 Linear와 Slack에 같은 URL로 기록한다.
- Slack은 작업 시작, 수동 작업 요청, 작업 차단, 리뷰 준비 완료, 주간 현황에만 보내며 `.agents/skills/slack-report`의 렌더링·검증·봇 발송 경로만 사용한다. Slack 커넥터 직접 발송은 금지한다. URL은 항상 `<URL|표시 이름>`으로 쓴다.

## 문서 관리

- `ADR.md`: 중요한 구조·기술 결정의 이유와 트레이드오프를 기록·갱신한다.
- `ARCHITECTURE.md`: 도메인 경계, 패키지, 인증·요청·외부 연동 흐름이 바뀌면 같은 작업에서 갱신한다.
- `docs/development-workflow.md`: 사람이 읽는 상세 운영 흐름과 메시지 템플릿이다.
- `.codex/`와 로컬 비밀값은 개인용이며 Git에 올리지 않는다.
