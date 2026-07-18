---
name: implement
description: Use only after the user explicitly says "계획 승인 및 구현 시작" for a plan developed with the gachi-server plan skill. It converts the approved plan into a Linear issue, branch, Slack start notification, implementation, verification, and Draft PR.
---

# 승인된 계획 구현

승인된 계획을 실행한다. 이미 결정된 범위를 다시 협상하지 않으며, 새 사실이 계획과 충돌할 때만 사용자에게 알린다.

1. 최신 Figma Feature list를 다시 확인하고 계획의 기준과 다르면 중단해 반영 범위를 묻는다.
2. gachi-server Linear 프로젝트에 이슈를 만들고 승인된 계획의 목표, 화면 흐름, API/도메인 범위, 완료 조건, 제외 범위, Figma 링크를 기록한다.
3. 최신 `origin/dev` 기반 Worktree와 `feat|fix|refactor|chore/jin-<번호>-<흐름>` 브랜치를 만든다. Linear를 In Progress로 옮기고 `docs/development-workflow.md`의 작업 시작 템플릿으로 Slack에 알린다.
4. 코드 작업은 Red → Green → Refactor 순서로 수행한다. REST API 변경 시 실제 로컬 HTTP API 테스트를 실행한다.
5. 검증 후 한국어 PR 템플릿을 완성하고 Draft PR을 생성한다. 정식 URL을 확인해 Linear를 In Review로 옮기고 리뷰 준비 Slack 템플릿을 보낸다.

REST API 변경이 없으면 PR의 API 테스트 섹션에 `미실시(REST API 변경 없음)`과 실행한 검증을 기록한다.
