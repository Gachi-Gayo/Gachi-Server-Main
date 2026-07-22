# CLAUDE.md

이 저장소의 공통 작업 규칙은 `AGENTS.md`(Codex 에이전트용으로 작성됨)를 그대로 따른다. `AGENTS.md`가 유일한 소스이며, 이 파일은 Codex 전용 표현을 Claude Code 쪽 도구로 대응시키는 얇은 어댑터일 뿐이다. `AGENTS.md`가 갱신되면 이 매핑도 함께 확인한다.

## 도구 대응표

| AGENTS.md의 표현 | Claude Code에서 |
| --- | --- |
| 계획 모드 (`.agents/skills/plan`) | Claude Code 내장 Plan Mode를 사용하되, `plan` 스킬의 경계(코드/Linear 이슈/브랜치·Worktree/Slack 알림을 만들지 않음)를 동일하게 적용한다. |
| `계획 승인 및 구현 시작` → `.agents/skills/implement` | 동일한 문구를 구현 전환 신호로 삼고, `implement` 스킬 절차를 그대로 수행한다. |
| Codex Worktree | `git worktree add -b <branch> <path> origin/dev`, 또는 현재 워크트리에서 바로 전환할 때는 `git switch -c <branch> origin/dev`. |
| Codex App에서 완료 알림 수신 (`slack-report`의 `manual` 보고) | Slack이 아니라 이 Claude Code 세션 채팅으로 직접 알린다. |
| `.agents/skills/<name>` | `.claude/skills/<name>` (내용은 `.agents/skills/<name>/SKILL.md`를 참조하는 얇은 스킬 파일). |

## 핵심 규칙 (요약, 본문은 AGENTS.md 참고)

- 브랜치: 최신 `origin/dev`에서 `feat|fix|refactor|chore/jin-<번호>-<흐름>` 형식으로 생성. `dev`에 직접 코드 변경 금지.
- Linear 상태: `Backlog → Todo → In Progress → In Review → Done`, PR 병합 후에만 Done.
- REST API 변경 시 Controller `@Operation`/`@ApiResponse`, DTO `@Schema`, 에러 계약, 테스트, Swagger를 같은 변경에서 갱신하고 실제 로컬 HTTP 호출로 검증한다.
- 사용자에게 보이는 Linear 이슈·Slack 메시지·PR 제목/본문은 한국어로 작성한다.
- PR은 `.github/PULL_REQUEST_TEMPLATE.md`의 모든 섹션을 채운다.
- Slack은 작업 시작·수동 작업 요청·작업 차단·리뷰 준비 완료·주간 현황 다섯 가지에만, `.claude/skills/slack-report`(≒ `.agents/skills/slack-report`)의 렌더링·검증·봇 발송 경로로만 보낸다. Slack 커넥터로 직접 발송하지 않는다.

## 문서 관리

`ADR.md`, `ARCHITECTURE.md`, `docs/development-workflow.md`는 AGENTS.md와 동일하게 취급한다. `.codex/`, `.claude/settings.local.json`, 로컬 비밀값은 개인용이며 Git에 올리지 않는다.
