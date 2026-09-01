# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before doing project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, or local unstated state.

Repository: brasilia736211600-netizen/HebLibre

Read first, in this order:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md (only if present; if absent, do not invent it)
3. The current HEAD and branch directly from GitHub
4. The latest relevant commits, open PRs, and CI status for the current branch

Then execute exactly:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE

Rules:
- GitHub is the source of truth.
- Never redo a completed verified step unless new evidence makes it necessary.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- Apply YAGNI: do not add architecture, dependencies, abstractions, or features without a demonstrated need.
- TDD first for new behavior whenever a deterministic non-device test is possible.
- Android runtime testing is optional until the code path is otherwise prepared; do not block all development on having a phone available.
- Do not touch unrelated files.
- Do not resurrect or use old WebLibre project state documents; this repository has its own state system.
- Use Genspark credits primarily for deep repository analysis, difficult debugging, substantial multi-file implementation, and long test/verification loops. Use cheap/local orchestration for state checks, GitHub inspection, small documentation edits, and bounded deterministic work.
- When a long Genspark task is running, continue independent non-conflicting verification/work instead of waiting idle.

At the end of every completed substantive step:
1. verify the exact branch and HEAD,
2. record tests and CI evidence,
3. update docs/HEBLIBRE_WORKFLOW_STATE.md,
4. record the SINGLE NEXT EXECUTION STEP,
5. stop before starting an unrelated next phase.

Start from the current state recorded in docs/HEBLIBRE_WORKFLOW_STATE.md and continue from its SINGLE NEXT EXECUTION STEP.
```

## Current resume target
At the time this file was created, the next step is:
`P0 — Profile / Identity Isolation — TDD Discovery` on branch `genspark-dev`, starting from the latest recorded HEAD in `docs/HEBLIBRE_WORKFLOW_STATE.md`.
