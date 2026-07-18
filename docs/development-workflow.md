# gachi-server 개발 워크플로우

## 두 가지 시작 방식

| 방식 | 시작 문구 | 승인 전 동작 |
| --- | --- | --- |
| 계획 모드 | `/plan <아이디어>` | Figma·코드 조사, 질문, 대안과 트레이드오프, 계획서 작성만 수행 |
| 즉시 구현 | `바로 구현해줘: <내용>` | Figma 확인 후 Linear·브랜치·Slack·구현을 바로 진행 |

계획 모드의 실행 전환 문구는 **`계획 승인 및 구현 시작`**이다. 승인 전에는 Linear, Slack, 브랜치, Worktree, 코드·문서 파일을 변경하지 않는다.

## 승인 후 흐름

```text
Figma 재확인 → Linear 이슈 → Worktree/브랜치 → Slack 시작
→ Red/Green/Refactor → API 테스트 → Draft PR → Linear In Review → Slack 리뷰 준비
```

## Slack 템플릿

작업 시작·수동 작업 요청·작업 차단·리뷰 준비 완료·주간 현황은 `.agents/skills/slack-report`의 렌더링·검증·봇 발송 경로만 사용한다. Slack 커넥터 직접 발송은 금지한다.

### 작업 시작

```md
*🚀 작업 시작* · `JIN-<번호>` · <흐름 이름>

• *Linear:* <https://linear.app/...|JIN-<번호>>
• *브랜치:* `<branch>`
• *작업 범위:* <한 줄 요약>
• *예정 검증:* 단위 테스트[, API 테스트]
```

### 리뷰 준비 완료

```md
*✅ 리뷰 준비 완료* · `JIN-<번호>` · <흐름>

• *PR:* <https://github.com/<owner>/<repo>/pull/<number>|Draft PR #<number>>
• *구현 요약:* <한두 문장>
• *검증:* <단위·통합 테스트 결과>
• *API 테스트:* 총 <N>건 · PASS <n> · FAIL <n>
• *리뷰 시 확인:* <없으면 `없음`>
```
