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
- Android runtime verification may be deferred; do not block otherwise-ready engineering work on phone/emulator availability.
- Do not touch unrelated files.
- This repository is HebLibre. Do not resurrect or use old WebLibre project state documents.
- Genspark credits are exhausted. Continue using available GitHub/local capabilities; do not wait for Genspark and do not claim Genspark execution.
- Use available GitHub/local capabilities for repository inspection, deterministic implementation, tests, CI verification, and continuity updates.
- When a long task is running, continue independent non-conflicting verification/work instead of waiting idle.

At the end of every completed substantive step:
1. verify the exact branch and HEAD,
2. record tests and CI evidence,
3. update docs/HEBLIBRE_WORKFLOW_STATE.md,
4. record exactly ONE NEXT EXECUTION STEP,
5. stop before starting an unrelated phase.

Current verified project state must always be taken from GitHub, not from this resume command itself. If the state document conflicts with GitHub, reconcile the document before implementation.

Start from the current state recorded in docs/HEBLIBRE_WORKFLOW_STATE.md and continue from its SINGLE NEXT EXECUTION STEP. If that next step is stale, first perform READ/VERIFY/RECONCILE and replace it with the smallest evidence-backed next step.
```

## Current resume target
P1 Steps 1–7 are completed to their documented verification boundary. P1 Step 8 (Android runtime verification of active-profile switching) is **DEFERRED**, not failed and not a blocker for continued engineering. Do not add emulator/instrumentation infrastructure solely to manufacture this verification. When an Android runtime becomes available, perform the focused profile-switching runtime check and then update the verification level. Until then, continue only with evidence-backed engineering work that does not depend on device execution.
