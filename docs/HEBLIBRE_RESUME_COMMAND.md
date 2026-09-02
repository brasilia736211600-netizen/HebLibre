# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before doing project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or local unstated state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

Read first, in this order:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md
3. docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md
4. The current HEAD and branch directly from GitHub
5. The latest relevant commits and CI/check status for the current branch

Then execute exactly:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE

Tool policy:
- Use GitHub as the authoritative source and primary execution surface.
- Apply Codex Engineering Guardrails throughout the workflow: YAGNI, minimal scope, verification-level discipline, and evidence-based claims.
- Use Codex Process Jobs only for genuinely independent work units; do not parallelize trivial sequential changes.
- Use Codex Coordinator only when multiple active workstreams require coordination.
- Use CodeRabbit after substantive implementation/test work for an independent diff/security/code-quality review when the environment exposes it.
- Use Codex Advisor only for non-trivial architecture or implementation decisions.
- Use AI DevKit or Develoop only when they provide a concrete capability not already available through the existing toolchain.
- Use Plugin Autopilot only when plugin selection/orchestration is itself useful.
- Treat Yaps Memory as convenience only; never as project authority.
- Use Prompt Optimizer only when a demonstrated prompt-quality bottleneck exists; it is not a routine pipeline stage.

Rules:
- GitHub is the source of truth.
- Never redo a completed verified step unless new evidence makes it necessary.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- Apply YAGNI: do not add architecture, dependencies, abstractions, or features without a demonstrated need.
- TDD first for new behavior whenever a deterministic non-device test is possible.
- Android runtime verification may be deferred; do not block otherwise-ready engineering work on phone/emulator availability.
- Do not touch unrelated files.
- HebLibre is separate from the old WebLibre project state. Use WebLibre only as a feature/design source pool, not as HebLibre continuity state.
- Genspark credits are exhausted. Continue using available GitHub/local capabilities; do not wait for Genspark.
- Before implementing a feature, confirm from source whether HebLibre already has it; do not reimplement existing functionality.
- Keep each substantive step small and independently resumable.
- Prefer one clear next action over a long speculative roadmap.

At the end of every completed substantive step:
1. verify the exact branch and HEAD,
2. record tests and CI evidence,
3. run CodeRabbit review when the change is substantive and the review environment is available,
4. update docs/HEBLIBRE_WORKFLOW_STATE.md,
5. update docs/HEBLIBRE_MASTER_PROJECT_MAP.md when phase/roadmap/tooling policy changes,
6. update docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md when a feature-pool status changes,
7. record exactly ONE NEXT EXECUTION STEP,
8. verify remote HEAD.

Current authoritative next step: read docs/HEBLIBRE_WORKFLOW_STATE.md and execute its SINGLE NEXT EXECUTION STEP.
Current target: CI-verify the current GPC implementation at HEAD e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f; inspect the unit-test workflow result before selecting P2 Step 4.
```

## Current authoritative state
- Active branch: `genspark-dev`.
- Current HEAD: `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`.
- P1 profile/identity implementation reached its documented boundary; Android profile-switch runtime validation is deferred and is not a blocker.
- P2 Step 1 conservative tracking/query-parameter cleanup is complete.
- P2 Step 2 HTTPS-only navigation policy is complete and CI-VERIFIED by run `33650164143`.
- P2 Step 3 GPC policy, deterministic tests, and existing WebView request-header wiring are implemented at source/test level; current HEAD is not yet CI-VERIFIED.
- Genspark credits are exhausted; continue from GitHub/local capabilities.
```
