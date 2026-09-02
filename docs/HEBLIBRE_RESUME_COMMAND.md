# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before doing project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, or local unstated state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

Read first, in this order:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md
3. docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md
4. The current HEAD and branch directly from GitHub
5. The latest relevant commits and CI/check status for the current branch

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
- HebLibre is separate from the old WebLibre project state. Use the WebLibre repository only as a feature/design source pool, not as HebLibre continuity state.
- Genspark credits are exhausted. Continue using available GitHub/local capabilities; do not wait for Genspark.
- Before implementing a feature, confirm from source whether HebLibre already has it; do not reimplement existing functionality.
- Keep each substantive step small and independently resumable.

At the end of every completed substantive step:
1. verify the exact branch and HEAD,
2. record tests and CI evidence,
3. update docs/HEBLIBRE_WORKFLOW_STATE.md,
4. update docs/HEBLIBRE_MASTER_PROJECT_MAP.md when the phase/roadmap changes,
5. record exactly ONE NEXT EXECUTION STEP,
6. verify remote HEAD.

Current authoritative next step: read docs/HEBLIBRE_WORKFLOW_STATE.md and follow its SINGLE NEXT EXECUTION STEP. Current target: P2 Step 2 — source-verify HTTPS-only mode and implement only the smallest evidence-backed local navigation policy seam if supported by the existing WebView architecture.
```

## Current authoritative state
- Active branch: `genspark-dev`.
- P1 profile/identity implementation reached its documented boundary; Android profile-switch runtime validation is deferred and is not a blocker.
- `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` records the comparison between existing HebLibre functionality and the separate WebLibre feature pool.
- P2 Step 1 conservative tracking/query-parameter cleanup is implemented and CI-VERIFIED by run `33648307698` on the feature HEAD.
- Current next implementation target: **P2 Step 2 — source verification of HTTPS-only mode in the existing WebView navigation path; make the smallest deterministic TDD-backed change only if the architecture supports it without new networking architecture.**
