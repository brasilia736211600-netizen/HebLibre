# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before doing project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, or local unstated state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

Read first, in this order:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md (only if present; if absent, do not invent it)
3. The current HEAD and branch directly from GitHub
4. The latest relevant commits, open PRs, and CI/check status for the current branch

Then execute exactly:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE

Rules:
- GitHub is the source of truth.
- Never redo a completed verified step unless new evidence makes it necessary.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- Apply YAGNI: do not add architecture, dependencies, abstractions, or features without a demonstrated need.
- TDD first for new behavior whenever a deterministic non-device test is possible.
- Android runtime testing is optional until the code path is otherwise prepared; do not block development on phone availability.
- Do not touch unrelated files.
- This repository is HebLibre. Do not resurrect or use old WebLibre project state documents.
- Use Genspark credits only for deep repository analysis, difficult debugging, substantial multi-file implementation, and long verification loops. Use low-cost/local orchestration for state checks, GitHub inspection, small documentation edits, and bounded deterministic work.
- Genspark credits are currently exhausted. Continue using available GitHub/local capabilities; do not wait for Genspark and do not claim Genspark execution.
- When a long task is running, continue independent non-conflicting verification/work instead of waiting idle.

At the end of every completed substantive step:
1. verify the exact branch and HEAD,
2. record tests and CI evidence,
3. update docs/HEBLIBRE_WORKFLOW_STATE.md,
4. record exactly ONE NEXT EXECUTION STEP,
5. stop before starting an unrelated phase.

Current verified project state must always be taken from GitHub, not from the resume command itself. If the state document conflicts with GitHub, reconcile the document before implementation.

Start from the current state recorded in docs/HEBLIBRE_WORKFLOW_STATE.md and continue from its SINGLE NEXT EXECUTION STEP. If that next step is stale, first perform READ/VERIFY/RECONCILE and replace it with the smallest evidence-backed next step.
```

## Current resume target
P1 persisted whitelist profile-id migration is complete. The next decision/implementation target must be derived from the current `docs/HEBLIBRE_WORKFLOW_STATE.md` and verified against the live `genspark-dev` HEAD before any code change. Do not assume that a profile-switching UI is authorized merely because the persistence layer can accept a non-default profile id.
