---
name: plan
description: Use whenever the user wants to discuss, plan, design, review, or clarify a gachi-server feature before implementation. This skill creates and evolves an approval-ready plan without creating Linear issues, branches, Slack messages, commits, or code changes.
---

# 승인 전 계획 모드

이 스킬은 `.agents/skills/plan/SKILL.md`(유일한 소스)의 절차·경계·계획서 형식을 그대로 따른다.

## Claude Code에서의 차이

Claude Code에는 내장 Plan Mode(읽기 전용 조사 → 계획서 작성 → `ExitPlanMode`로 승인 요청)가 이미 있다. 이 프로젝트에서 Plan Mode를 쓸 때도 아래 경계를 동일하게 적용한다:

- 최신 Figma Feature list, 관련 프레임, `ADR.md`, `ARCHITECTURE.md`, 기존 코드는 읽을 수 있다.
- Linear 이슈 생성·상태 변경, Worktree·브랜치 생성, Slack 발송, 코드·문서 변경, 커밋·푸시는 하지 않는다.
- 사용자가 정확히 `계획 승인 및 구현 시작`이라고 말하기 전에는(또는 Claude Code의 `ExitPlanMode` 승인 직후에도) 구현을 시작하지 않는다 — 승인 신호가 명확할 때만 `.claude/skills/implement`로 넘어간다.

계획서 형식(목표와 제외 범위 / Figma·기존 코드 근거 / 사용자 흐름과 상태 / 선택지와 결정 / API·도메인·데이터·보안 영향 / 완료 조건과 검증 시나리오 / 구현 단계 / 열린 질문)은 원본 스킬과 동일하게 사용한다.
