import importlib.util
import pathlib
import unittest


SCRIPT = pathlib.Path(__file__).parents[1] / "scripts" / "slack_report.py"
SPEC = importlib.util.spec_from_file_location("slack_report", SCRIPT)
slack_report = importlib.util.module_from_spec(SPEC)
SPEC.loader.exec_module(slack_report)


class SlackReportTest(unittest.TestCase):
    def test_renders_every_fixed_report_template(self):
        reports = [
            ("start", {
                "issue": "JIN-17", "flow": "Slack 봇 고정 보고 자동화",
                "linear_url": "https://linear.app/jinhyeongpark/issue/JIN-17/slack-봇-고정-보고-자동화",
                "branch": "chore/jin-17-slack-report-bot", "scope": "고정 보고 자동화 구축",
                "verification": "단위 테스트",
            }),
            ("manual", {"issue": "JIN-17", "flow": "Slack 봇 고정 보고 자동화", "reason": "권한 승인 필요", "action": "1. Slack 앱 권한을 승인합니다.", "completion": "봇이 채널에 메시지를 보냅니다."}),
            ("blocked", {"issue": "JIN-17", "flow": "Slack 봇 고정 보고 자동화", "reason": "봇 토큰이 없습니다.", "impact": "발송 검증이 지연됩니다.", "decision": "토큰을 설정합니다(권장)."}),
            ("review", {"issue": "JIN-17", "flow": "Slack 봇 고정 보고 자동화", "pr_url": "https://github.com/Gachi-Gayo/Gachi-Server-Main/pull/20", "pr_number": 20, "summary": "스킬과 검증 스크립트를 추가했습니다.", "verification": "테스트 통과", "api_test": "미실시(REST API 변경 없음) · Python 단위 테스트", "review_focus": "없음"}),
            ("weekly", {"done": ["JIN-17 · 자동화 구현"], "in_progress": ["JIN-18 · 검토"], "blocked": [], "next": ["JIN-19 · 배포"]}),
        ]

        for report_type, data in reports:
            with self.subTest(report_type=report_type):
                rendered = slack_report.render(report_type, data)
                self.assertTrue(rendered)
                self.assertIn("\n\n", rendered)
                self.assertEqual([], slack_report.validate(report_type, data, rendered))

    def test_rejects_invalid_link_and_missing_required_value(self):
        data = {
            "issue": "JIN-17", "flow": "Slack 봇 고정 보고 자동화",
            "linear_url": "not-a-url", "branch": "chore/jin-17-slack-report-bot",
            "scope": "", "verification": "단위 테스트",
        }
        errors = slack_report.validate("start", data)
        self.assertTrue(any("scope" in error for error in errors))
        self.assertTrue(any("linear_url" in error for error in errors))

    def test_post_requires_token_without_exposing_it(self):
        with self.assertRaisesRegex(RuntimeError, "SLACK_BOT_TOKEN"):
            slack_report.post("테스트", token="")

    def test_post_rejects_any_channel_other_than_configured_channel(self):
        with self.assertRaisesRegex(ValueError, "C0BJ75AF9NE"):
            slack_report.post("테스트", token="token", channel="C123")


if __name__ == "__main__":
    unittest.main()
