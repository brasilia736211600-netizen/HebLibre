# HebLibre Resume Command

Paste the following instruction into any new chat or coding agent before project work.

```text
@GitHub @Thinking

Resume HebLibre from GitHub only. Do NOT rely on chat memory, prior-agent memory, plugin memory, or unstated local state.

Repository: brasilia736211600-netizen/HebLibre
Active branch: genspark-dev

Read first:
1. docs/HEBLIBRE_WORKFLOW_STATE.md
2. docs/HEBLIBRE_MASTER_PROJECT_MAP.md
3. docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md
4. Verify current branch and HEAD directly from GitHub
5. Verify latest relevant CI/check state

Execute exactly:
READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE

Tool policy:
- GitHub is authoritative and the primary execution surface.
- Apply Codex Engineering Guardrails: YAGNI, minimal scope, verification discipline, evidence-based claims.
- Use Process Jobs only for genuinely independent work units.
- Use Coordinator only when multiple workstreams actually need orchestration.
- Use CodeRabbit for substantive review when its required local CLI/repository surface is available; never substitute it for tests. If unavailable, do not claim a CodeRabbit result.
- Use Codex Advisor only for non-trivial decisions.
- Use AI DevKit or Develoop only for concrete capabilities not already available.
- Use Plugin Autopilot only when plugin orchestration is itself useful.
- Treat Yaps Memory as convenience only; never as authority.
- Use Prompt Optimizer only for a demonstrated prompt-quality bottleneck.

Rules:
- Never redo completed verified work without new evidence.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- TDD first whenever deterministic JVM testing is possible.
- Before adding any feature, inspect current source because older matrix entries can be stale.
- Apply YAGNI; no architecture, dependency, abstraction, or subsystem replacement without demonstrated need.
- WebLibre is a separate project and only a feature/design source pool.
- Do not block engineering on Android runtime availability.
- IMPORTANT: do not build/install/test the Android APK after each feature. Continue source/JVM/CI work and postpone Android runtime validation until the planned feature set is mature enough for one final device pass. Fix runtime regressions discovered during that final pass afterward.
- When two work units are genuinely independent, investigate/execute them in parallel where the tool surface allows; serialize only dependent branch mutations.
- Keep each substantive step small and independently resumable.

At the end of each substantive step:
1. verify exact branch and HEAD,
2. record tests and CI/runtime evidence,
3. run CodeRabbit when available and appropriate,
4. update docs/HEBLIBRE_WORKFLOW_STATE.md,
5. update docs/HEBLIBRE_MASTER_PROJECT_MAP.md when phase/roadmap/tooling changes,
6. update docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md when feature status changes,
7. record exactly ONE next execution step,
8. verify remote HEAD.

Current authoritative next step:
CI-verify the current privacy-control feature batch whose source/test tip is `aa5fdace59a746359870a09bfd43644c5e07aeb6` (bounded WebView media-permission guard + optional third-party cookie blocking). Reconcile the result before selecting the next bounded feature. Do not install the APK yet.

Final-device rule:
Continue implementing and CI-verifying bounded features first. Reserve Android build/install/runtime verification for the final validation phase. Only after that pass should runtime regressions drive additional fixes.
```

## Current authoritative checkpoint
- Active branch: `genspark-dev`.
- Current branch includes the documentation checkpoints following the privacy-control implementation batch.
- P2.1–P2.6 are CI-VERIFIED.
- P2.7 media permission guard and P2.8 third-party cookie blocking are source/test implemented; the feature batch is the current CI checkpoint.
- Android runtime validation is deliberately reserved for the final device pass.
