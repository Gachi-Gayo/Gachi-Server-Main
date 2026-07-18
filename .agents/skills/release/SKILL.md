---
name: release
description: Use when the user asks to prepare a dev-to-main deployment pull request. It analyzes release scope, creates a Korean Draft PR, and reports it to Slack without merging or deploying.
---

# dev → main 배포 PR

사용자가 `/release dev를 main 배포 PR로 올려줘`와 같이 요청하면, `dev`의 검증된 변경을 `main`으로 배포하기 위한 **Draft PR만** 준비한다.

## 절대 경계

- `main`에 직접 push하거나 PR을 merge하지 않는다.
- GitHub Actions를 수동 실행하거나 EC2·Docker 등 운영 환경에 배포하지 않는다.
- 사용자가 GitHub에서 Draft PR을 검토한 뒤 직접 merge한다. 현재 배포 워크플로는 `main` merge 뒤에만 동작한다.
- 배포 범위를 바꾸는 충돌, 미검증 변경, 민감정보 노출 가능성이 있으면 Draft PR을 만들지 말고 Linear에 사유를 기록하고 Slack으로 차단을 알린다.

## 절차

1. `dev`와 `main`의 최신 원격 상태, 열린 `dev → main` PR 유무, 작업 트리를 확인한다. 이미 열린 배포 PR이 있으면 새 PR을 만들지 않고 그 PR을 갱신 대상으로 안내한다.
2. `main...dev` 비교로 커밋·파일 변경을 분석하고, 포함된 PR과 Linear 이슈를 확인한다.
3. 변경을 다음 항목으로 분류한다: 기능/버그, REST API 계약, 환경 변수·시크릿, DB 마이그레이션, 인프라·운영 설정, 롤백 영향.
4. 포함 변경의 테스트·빌드 결과를 수집한다. 결과가 없거나 실패하면 이를 명시하고, 배포 전 필요한 검증을 작성한다.
5. `.github/PULL_REQUEST_TEMPLATE/release.md`의 모든 항목을 한국어로 채워 `dev`를 head, `main`을 base로 하는 Draft PR을 생성한다. 제목은 `chore: <버전 또는 날짜> 배포` 형식을 사용한다.
6. GitHub가 반환한 PR URL이 실제로 열리는지 확인한 뒤, 연결된 Linear 이슈를 `In Review`로 변경하고 고정된 “리뷰 준비 완료” Slack 템플릿으로 알린다.

## PR 본문 필수 내용

- 배포 범위와 포함 PR/Linear 이슈
- REST API, 환경 변수·시크릿, DB 마이그레이션, 인프라 영향 및 해당 없음 여부
- 실행한 검증과 결과
- 배포 후 확인 항목 및 롤백 기준
- `main` merge와 실제 배포는 사용자가 직접 수행한다는 안내
