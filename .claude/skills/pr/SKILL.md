---
name: pr
description: Use when a verified gachi-server change is ready for review. Commit intentional changes, push, create a Korean Draft PR to dev, and update Linear and Slack.
---

# Draft PR

이 스킬은 `.agents/skills/pr/SKILL.md`(유일한 소스)의 절차를 그대로 따른다 — 툴에 종속적인 내용이 없으므로 변경 없이 그대로 적용한다:

1. `git status`, diff, 검증 결과를 확인하고 비밀값은 제외한다.
2. 한국어 제목·본문으로 `.github/PULL_REQUEST_TEMPLATE.md`의 모든 섹션을 채운다.
3. REST API 변경 시 실제 API 테스트 표를, 없으면 `미실시(REST API 변경 없음)`과 실행 테스트를 기록한다.
4. 커밋·푸시하고 `dev` 대상 Draft PR을 만든다. 정식 URL이 열리는지 확인한다.
5. Linear를 In Review로 옮기고 `docs/development-workflow.md`의 리뷰 준비 Slack 템플릿을 `.claude/skills/slack-report`로 발송한다.

관련 Linear 이슈가 없는 예외 작업(부트스트랩/인프라성 메타 작업 등)인 경우, PR 본문의 Linear 관련 섹션에 그 사유를 명시한다.
