---
name: new-branch
description: Use when an approved gachi-server implementation needs a new branch. Create a Linear-backed branch from latest origin/dev without mixing local changes.
---

# 새 브랜치

이 스킬의 절차는 `.agents/skills/new-branch/SKILL.md`(Codex용, 유일한 소스)를 그대로 따른다. Claude Code에서의 차이는 하나뿐이다:

- "Codex Worktree" 대신 `git worktree add -b feat|fix|refactor|chore/jin-<번호>-<흐름> <경로> origin/dev` 또는 현재 워크트리에서 `git switch -c <같은 형식> origin/dev`를 사용한다.

나머지(Linear 이슈 키 선확보, `origin/dev` 기준 분기, `codex/` 임시 접두사 제거 규칙과 동일하게 임시 접두사를 최종 브랜치명에 남기지 않는 것)는 원본 스킬과 동일하다.
