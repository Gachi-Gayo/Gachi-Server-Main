---
name: new-branch
description: Use when an approved gachi-server implementation needs a new branch. Create a Linear-backed branch from latest dev without mixing local changes.
---

# 새 브랜치

1. Linear 이슈 키가 없으면 먼저 만든다.
2. 더러운 로컬 작업 트리에서는 Codex Worktree를 사용한다.
3. `origin/dev`에서 `feat|fix|refactor|chore/jin-<번호>-<흐름>` 브랜치를 만든다.
4. `codex/` 자동 접두사는 최종 브랜치에 남기지 않는다.
