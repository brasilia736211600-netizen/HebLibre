# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before doing project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or unstated local state.

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
- Apply Codex Engineering Guardrails throughout: YAGNI, minimal scope, verification-level discipline, and evidence-based claims.
- Use Codex Process Jobs only for genuinely independent work units; do not decompose small tasks unnecessarily.
- Use Codex Coordinator only when multiple workstreams are active or dependencies must be coordinated; do not add coordination overhead to one local change.
- Use CodeRabbit after substantive implementation/test work for an independent diff/security/code-quality review when available; never substitute it for tests. If its required local CLI/repository surface is unavailable, record that fact and do not claim a CodeRabbit result.
- Use Codex Advisor only at non-trivial engineering decision points.
- Use AI DevKit or Develoop only when they provide a concrete capability not already available.
- Use Plugin Autopilot only when plugin selection/orchestration is itself the task.
- Treat Yaps Memory as convenience only; never as project authority.
- Use Prompt Optimizer only when a demonstrated prompt-quality bottleneck exists.

Rules:
- GitHub is the source of truth.
- Never redo a completed verified step unless new evidence makes it necessary.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- Apply YAGNI: no architecture, dependencies, abstractions, or features without demonstrated need.
- TDD first for new behavior whenever a deterministic non-device test is possible.
- Android runtime verification may be deferred; do not block otherwise-ready engineering work on phone/emulator availability.
- Do not touch unrelated files.
- HebLibre is separate from the old WebLibre project state. Use WebLibre only as a feature/design source pool.
- Genspark credits are exhausted. Continue through available GitHub/local capabilities; do not wait for Genspark.
- Before implementing any feature, confirm from source whether HebLibre already has it. A feature listed as missing in an older matrix must not be reimplemented without current source verification.
- Keep each substantive step small and independently resumable.
- Prefer one clear next action over a speculative roadmap.
- When two work units are genuinely independent, investigate/execute them in parallel where the tool surface permits; serialize only the branch mutations that depend on each other.

At the end of every completed substantive step:
1. verify exact branch and HEAD,
2. record tests and CI evidence,
3. run CodeRabbit when the change is substantive and the review environment is available,
4. update docs/HEBLIBRE_WORKFLOW_STATE.md,
5. update docs/HEBLIBRE_MASTER_PROJECT_MAP.md when phase/roadmap/tooling policy changes,
6. update docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md when a feature-pool status changes,
7. record exactly ONE NEXT EXECUTION STEP,
8. verify remote HEAD.

Current authoritative next step:
Read docs/HEBLIBRE_WORKFLOW_STATE.md and docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md, then source-verify Screenshot Protection. Confirm whether `FLAG_SECURE` or an equivalent protection already exists. Because this feature is Android-window scoped rather than a meaningful pure-Java behavior, use a bounded source trace first and only introduce a test seam if it has independent value.

Current verified checkpoint:
P2 Steps 1–4 are complete and CI-VERIFIED. Desktop Mode feature HEAD `c5c9e77abe8df400bc902099a7877ec6a3d1fc51` passed Actions run `33677771905`; the branch subsequently advanced with documentation commits.
```

## Current authoritative state
- Active branch: `genspark-dev`.
- Current branch HEAD is maintained in `docs/HEBLIBRE_WORKFLOW_STATE.md`; always verify it directly from GitHub before work.
- P2 Steps 1–4 are complete and CI-VERIFIED.
- P1 profile/identity groundwork is complete within its documented boundary; Android runtime validation remains deferred.
- Clear-on-exit is source-verified as already implemented; do not reimplement it.
- OLED/AMOLED pure-black support is source-verified as already implemented; do not reimplement it.
- Genspark credits are exhausted; continue through GitHub/local capabilities.
- Next task is bounded source verification of Screenshot Protection.
