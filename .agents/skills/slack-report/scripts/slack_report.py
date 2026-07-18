#!/usr/bin/env python3
"""Render, validate, and post the gachi-server's fixed Slack reports."""
from __future__ import annotations

import argparse
import json
import os
import re
import sys
import urllib.error
import urllib.request
from typing import Any

CHANNEL_ID = "C0BJ75AF9NE"
SLACK_POST_MESSAGE_URL = "https://slack.com/api/chat.postMessage"
ISSUE_RE = re.compile(r"^JIN-\d+$")
URL_RE = re.compile(r"^https://[^\s<>]+$")
REQUIRED_FIELDS = {
    "start": ("issue", "flow", "linear_url", "branch", "scope", "verification"),
    "manual": ("issue", "flow", "reason", "action", "completion"),
    "blocked": ("issue", "flow", "reason", "impact", "decision"),
    "review": ("issue", "flow", "pr_url", "pr_number", "summary", "verification", "api_test", "review_focus"),
    "weekly": ("done", "in_progress", "blocked", "next"),
}


def _list_items(items: list[str], empty: str = "없음") -> str:
    return "\n".join(f"• {item}" for item in items) if items else f"• {empty}"


def validate(report_type: str, data: dict[str, Any], rendered: str | None = None) -> list[str]:
    """Return validation messages without inspecting or returning a bot token."""
    if report_type not in REQUIRED_FIELDS:
        return [f"지원하지 않는 보고 유형입니다: {report_type}"]
    errors: list[str] = []
    for field in REQUIRED_FIELDS[report_type]:
        value = data.get(field)
        if value is None or value == "" or (value == [] and report_type != "weekly"):
            errors.append(f"필수 값이 비어 있습니다: {field}")
    if report_type != "weekly" and data.get("issue") and not ISSUE_RE.fullmatch(str(data["issue"])):
        errors.append("issue는 JIN-<번호> 형식이어야 합니다.")
    for field in ("linear_url", "pr_url"):
        if data.get(field) and not URL_RE.fullmatch(str(data[field])):
            errors.append(f"{field}은 https URL이어야 합니다.")
    if report_type == "weekly":
        for field in REQUIRED_FIELDS["weekly"]:
            if field in data and not isinstance(data[field], list):
                errors.append(f"{field}은 목록이어야 합니다.")
    if rendered is not None:
        if "\n\n" not in rendered:
            errors.append("제목 다음에는 빈 줄이 하나 있어야 합니다.")
        if "[Started]" in rendered or "[Ready for review]" in rendered:
            errors.append("이전 대괄호 표기를 사용할 수 없습니다.")
        if re.search(r"(?<!<)https://[^\s>|]+(?!\|)", rendered):
            errors.append("Slack URL은 <URL|표시 이름> 형식이어야 합니다.")
    return errors


def render(report_type: str, data: dict[str, Any]) -> str:
    """Render one of the five fixed Markdown report templates."""
    errors = validate(report_type, data)
    if errors:
        raise ValueError("\n".join(errors))
    if report_type == "start":
        text = f"*🚀 작업 시작* · `{data['issue']}` · {data['flow']}\n\n• *Linear:* <{data['linear_url']}|{data['issue']}>\n• *브랜치:* `{data['branch']}`\n• *작업 범위:* {data['scope']}\n• *예정 검증:* {data['verification']}"
    elif report_type == "manual":
        text = f"*⚠️ 수동 작업 필요* · `{data['issue']}` · {data['flow']}\n\n• *필요한 이유:* {data['reason']}\n• *해야 할 일:* {data['action']}\n• *완료 기준:* {data['completion']}\n• *완료 후:* Codex App에서 `설정 완료`라고 알려주세요."
    elif report_type == "blocked":
        text = f"*🚧 작업 차단* · `{data['issue']}` · {data['flow']}\n\n• *막힌 이유:* {data['reason']}\n• *영향:* {data['impact']}\n• *필요한 결정:* {data['decision']}"
    elif report_type == "review":
        text = f"*✅ 리뷰 준비 완료* · `{data['issue']}` · {data['flow']}\n\n• *PR:* <{data['pr_url']}|Draft PR #{data['pr_number']}>\n• *구현 요약:* {data['summary']}\n• *검증:* {data['verification']}\n• *API 테스트:* {data['api_test']}\n• *리뷰 시 확인:* {data['review_focus']}"
    else:
        text = "*📌 주간 현황* · gachi-server\n\n*완료*\n" + _list_items(data["done"]) + "\n\n*진행 중*\n" + _list_items(data["in_progress"]) + "\n\n*차단*\n" + _list_items(data["blocked"]) + "\n\n*다음 예정*\n" + _list_items(data["next"])
    errors = validate(report_type, data, text)
    if errors:
        raise AssertionError("렌더링된 보고 형식 오류: " + "; ".join(errors))
    return text


def post(text: str, token: str | None = None, channel: str = CHANNEL_ID) -> str:
    """Post only to the fixed channel and return its Slack message timestamp."""
    if channel != CHANNEL_ID:
        raise ValueError(f"보고 채널은 {CHANNEL_ID}로 고정되어 있습니다.")
    token = token if token is not None else os.environ.get("SLACK_BOT_TOKEN", "")
    if not token:
        raise RuntimeError("SLACK_BOT_TOKEN 환경 변수가 설정되어 있지 않습니다.")
    payload = json.dumps({"channel": channel, "text": text}).encode("utf-8")
    request = urllib.request.Request(SLACK_POST_MESSAGE_URL, data=payload, headers={"Authorization": f"Bearer {token}", "Content-Type": "application/json; charset=utf-8"}, method="POST")
    try:
        with urllib.request.urlopen(request, timeout=15) as response:
            body = json.loads(response.read().decode("utf-8"))
    except urllib.error.HTTPError as error:
        raise RuntimeError(f"Slack API 요청에 실패했습니다(HTTP {error.code}).") from error
    except urllib.error.URLError as error:
        raise RuntimeError("Slack API 연결에 실패했습니다.") from error
    if not body.get("ok"):
        raise RuntimeError(f"Slack API가 메시지를 거부했습니다: {body.get('error', 'unknown_error')}")
    return str(body["ts"])


def _load_data(path: str) -> dict[str, Any]:
    with open(path, encoding="utf-8") as file:
        value = json.load(file)
    if not isinstance(value, dict):
        raise ValueError("입력 JSON은 객체여야 합니다.")
    return value


def main() -> int:
    # Windows consoles may default to cp949, which cannot print emoji used by the fixed templates.
    if hasattr(sys.stdout, "reconfigure"):
        sys.stdout.reconfigure(encoding="utf-8")
        sys.stderr.reconfigure(encoding="utf-8")
    parser = argparse.ArgumentParser(description="Render, validate, or post a fixed gachi-server Slack report.")
    parser.add_argument("action", choices=("render", "validate", "send"))
    parser.add_argument("report_type", choices=tuple(REQUIRED_FIELDS))
    parser.add_argument("--data", required=True, help="UTF-8 JSON input file")
    args = parser.parse_args()
    try:
        data = _load_data(args.data)
        if args.action == "validate":
            errors = validate(args.report_type, data)
            if errors:
                raise ValueError("\n".join(errors))
            print("검증 통과")
            return 0
        text = render(args.report_type, data)
        if args.action == "render":
            print(text)
            return 0
        timestamp = post(text)
        print(f"발송 성공: channel={CHANNEL_ID}, ts={timestamp}")
        return 0
    except (OSError, ValueError, RuntimeError, AssertionError) as error:
        print(f"오류: {error}", file=sys.stderr)
        return 1


if __name__ == "__main__":
    raise SystemExit(main())
