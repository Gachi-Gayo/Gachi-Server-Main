---
name: release
description: Use when the user asks to prepare a dev-to-main deployment pull request. It analyzes release scope, creates a Korean Draft PR, and reports it to Slack without merging or deploying.
---

# dev → main 배포 PR

이 스킬은 `.agents/skills/release/SKILL.md`(유일한 소스)의 절차를 그대로 따른다 — 툴에 종속적인 내용이 없으므로 변경 없이 그대로 적용한다.

## 절대 경계

- `main`에 직접 push하거나 PR을 merge하지 않는다.
- GitHub Actions를 수동 실행하거나 EC2·Docker 등 운영 환경에 배포하지 않는다.
- 사용자가 GitHub에서 Draft PR을 검토한 뒤 직접 merge한다.
- 배포 범위를 바꾸는 충돌, 미검증 변경, 민감정보 노출 가능성이 있으면 Draft PR을 만들지 말고 Linear에 사유를 기록하고 Slack으로 차단을 알린다.

## 절차

1. `dev`와 `main`의 최신 원격 상태, 열린 `dev → main` PR 유무, 작업 트리를 확인한다.
2. `main...dev` 비교로 커밋·파일 변경을 분석하고, 포함된 PR과 Linear 이슈를 확인한다.
3. 변경을 기능/버그, REST API 계약, 환경 변수·시크릿, DB 마이그레이션, 인프라·운영 설정, 롤백 영향으로 분류한다.
4. 포함 변경의 테스트·빌드 결과를 수집한다. 결과가 없거나 실패하면 명시한다.
5. `.github/PULL_REQUEST_TEMPLATE/release.md`를 한국어로 채워 `dev`→`main` Draft PR을 생성한다. 제목은 `chore: <버전 또는 날짜> 배포`.
6. PR URL 확인 후 Linear를 `In Review`로 옮기고 `.claude/skills/slack-report`로 리뷰 준비 완료 템플릿을 보낸다.
