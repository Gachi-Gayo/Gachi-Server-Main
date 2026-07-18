---
name: plan
description: Use whenever the user wants to discuss, plan, design, review, or clarify a gachi-server feature before implementation. This skill creates and evolves an approval-ready plan without creating Linear issues, branches, Slack messages, commits, or code changes.
---

# 승인 전 계획 모드

사용자의 아이디어를 승인 가능한 구현 계획으로 발전시킨다. 이 단계의 목적은 성급한 구현이 아니라 요구사항과 선택지를 함께 확정하는 것이다.

## 경계

- 최신 Figma Feature list, 관련 프레임, `ADR.md`, `ARCHITECTURE.md`, 기존 코드는 읽을 수 있다.
- Linear 이슈 생성·상태 변경, Worktree·브랜치 생성, Slack 발송, 코드·문서 변경, 커밋·푸시는 하지 않는다.
- 사용자가 `계획 승인 및 구현 시작`이라고 정확히 말하기 전에는 구현을 시작하지 않는다.

## 대화 흐름

1. 사용자 목표, 대상 사용자, 시작·완료 조건을 요약하고 최신 Figma 근거를 확인한다.
2. 구현에 영향을 주는 모호함을 한 번에 가장 중요한 질문부터 묻는다.
3. 선택지가 둘 이상이면 옵션별 장점·트레이드오프·권장안을 짧게 제시한다. 사용자의 선택을 기록한다.
4. 매 응답 끝에 `결정됨`, `열린 질문`, `계획 초안`을 최신화한다.

## 계획서 형식

```md
# <흐름> 구현 계획
## 목표와 제외 범위
## Figma·기존 코드 근거
## 사용자 흐름과 상태
## 선택지와 결정
## API·도메인·데이터·보안 영향
## 완료 조건과 검증 시나리오
## 구현 단계
## 열린 질문
```

각 구현 단계에는 검증 방법을 적는다. REST API 변경 가능성이 있으면 Happy case와 의미 있는 edge case를 포함한다.

## 승인

`계획 승인 및 구현 시작`을 받으면 계획을 다시 요약하고 `.agents/skills/implement`로 넘긴다. 열린 질문이 구현을 막는 경우에만 승인 전에 해결을 요청한다.
