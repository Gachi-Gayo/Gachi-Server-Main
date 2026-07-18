---
name: api-test
description: Use whenever a newly added or changed gachi-server REST API needs end-to-end verification against a local server before a PR is ready.
---

# 실제 API 테스트

1. Controller, DTO, SecurityConfig를 읽어 메서드·경로·인증·오류 계약을 확정한다.
2. Happy case 1개와 의미 있는 edge case를 설계한다. 승인 대기는 하지 않는다.
3. 로컬 서버에 실제 HTTP 요청을 보내고 요청값·기대 응답·실제 응답·판정을 기록한다.
4. 테스트 데이터는 이 실행에서 만든 것만 정리하고, 기존 개발 데이터를 수정·삭제하지 않는다.
5. PR API 테스트 표에 결과를 남긴다. 환경이 준비되지 않으면 PASS로 처리하지 않고 차단 사유를 기록한다.
