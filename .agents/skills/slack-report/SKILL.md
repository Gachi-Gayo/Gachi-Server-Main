---
name: slack-report
description: gachi-server의 작업 시작, 수동 작업 요청, 작업 차단, 리뷰 준비 완료, 주간 현황을 Slack에 보고하거나 준비할 때 사용한다. 이 다섯 보고 유형은 반드시 이 스킬의 고정 템플릿 렌더링·검증·봇 발송 경로를 사용하며, Slack 커넥터 직접 발송은 사용하지 않는다.
---

# Slack 보고

이 스킬은 gachi-server의 정해진 다섯 보고를 같은 형식으로 전달한다. 문장을 새로 작성하지 말고 스크립트가 렌더링한 결과만 발송한다.

## 사용 범위

- `start`: 작업 시작
- `manual`: 수동 작업 요청
- `blocked`: 작업 차단
- `review`: PR 리뷰 준비 또는 작업 완료
- `weekly`: 주간 현황

일반 진행 로그와 단위 테스트 완료 알림에는 사용하지 않는다. Slack 커넥터 또는 `slack_send_message` 계열 도구로 직접 발송하지 않는다.

## 안전한 발송 절차

1. 보고 유형에 맞는 UTF-8 JSON 파일을 만든다. 토큰·시크릿·개인정보는 JSON에 넣지 않는다.
2. 먼저 검증한다.

   ```powershell
   python .agents/skills/slack-report/scripts/slack_report.py validate <유형> --data <입력.json>
   ```

3. 필요한 경우 렌더링 결과를 확인한다.

   ```powershell
   python .agents/skills/slack-report/scripts/slack_report.py render <유형> --data <입력.json>
   ```

4. 검증된 경우에만 봇으로 발송한다.

   ```powershell
   python .agents/skills/slack-report/scripts/slack_report.py send <유형> --data <입력.json>
   ```

스크립트는 `SLACK_BOT_TOKEN`을 프로세스 환경 변수에서만 읽고, `#p-gachi-server` (`C0BJ75AF9NE`) 외의 채널 전송을 거부한다. 성공 시 채널 ID와 메시지 `ts`만 출력한다. 토큰 값은 출력, 파일, Git, Linear, PR, Slack 메시지에 기록하지 않는다.

## JSON 입력

공통 필드인 `issue`는 `JIN-<번호>` 형식이다. URL 값은 일반 `https://` URL로 입력하며, 스크립트가 Slack `<URL|표시 이름>` 형식으로 바꾼다.

| 유형 | 필수 필드 |
| --- | --- |
| `start` | `issue`, `flow`, `linear_url`, `branch`, `scope`, `verification` |
| `manual` | `issue`, `flow`, `reason`, `action`, `completion` |
| `blocked` | `issue`, `flow`, `reason`, `impact`, `decision` |
| `review` | `issue`, `flow`, `pr_url`, `pr_number`, `summary`, `verification`, `api_test`, `review_focus` |
| `weekly` | `done`, `in_progress`, `blocked`, `next` (모두 문자열 목록) |

`review.api_test`에는 REST API 변경이 없으면 `미실시(REST API 변경 없음) · <실행한 테스트>`를 넣는다. `weekly.blocked`가 비어 있으면 스크립트가 `• 없음`을 렌더링한다.

## 검증 실패 처리

누락 필드, 잘못된 링크, 고정되지 않은 채널, 또는 환경 변수 누락으로 실패하면 발송하지 않는다. 수동 조치가 필요한 경우에는 `manual` 보고를 이 스킬으로 발송하고, 완료 후 재개 지시는 Slack이 아니라 Codex App에서 받는다.
