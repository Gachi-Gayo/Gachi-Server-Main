---
name: slack-report
description: gachi-server의 작업 시작, 수동 작업 요청, 작업 차단, 리뷰 준비 완료, 주간 현황을 Slack에 보고하거나 준비할 때 사용한다. 이 다섯 보고 유형은 반드시 이 스킬의 고정 템플릿 렌더링·검증·봇 발송 경로를 사용하며, Slack 커넥터 직접 발송은 사용하지 않는다.
---

# Slack 보고

이 스킬은 `.agents/skills/slack-report/SKILL.md`(유일한 소스)의 절차와 스크립트를 그대로 사용한다. 스크립트 경로(`Codex`/`Claude` 어느 쪽에서 실행하든 동일한 파일):

```powershell
python .agents/skills/slack-report/scripts/slack_report.py validate <유형> --data <입력.json>
python .agents/skills/slack-report/scripts/slack_report.py render <유형> --data <입력.json>
python .agents/skills/slack-report/scripts/slack_report.py send <유형> --data <입력.json>
```

## 사용 범위

`start`(작업 시작), `manual`(수동 작업 요청), `blocked`(작업 차단), `review`(PR 리뷰 준비/작업 완료), `weekly`(주간 현황). 일반 진행 로그·단위 테스트 완료 알림에는 사용하지 않으며, Slack 커넥터나 `slack_send_message` 계열 도구로 직접 발송하지 않는다.

## Claude Code에서의 차이

- 원본 스킬의 "완료 후 재개 지시는 Slack이 아니라 **Codex App**에서 받는다"는 문구는 Claude Code에서는 **이 세션 채팅으로 직접** 재개 지시를 받는 것으로 대응한다. 나머지 절차·검증 규칙은 동일하다.

## 안전 규칙 (동일)

스크립트는 `SLACK_BOT_TOKEN`을 프로세스 환경 변수에서만 읽고 `#p-gachi-server`(`C0BJ75AF9NE`) 외 채널 전송을 거부한다. 토큰 값은 출력·파일·Git·Linear·PR·Slack 메시지에 기록하지 않는다. 필수 필드·형식은 원본 스킬의 JSON 입력 표를 따른다.
