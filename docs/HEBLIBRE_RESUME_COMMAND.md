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

Continuous-work rule — IMPORTANT:
- When the user says `استمر` / `continue`, keep working continuously and autonomously instead of sending routine progress messages.
- Do not interrupt the workstream to report a discovery, warning, failing test, CI state, or newly found problem. Analyze it internally, fix it when justified, retest, and continue.
- When a better priority or required follow-up is discovered, change priority and execute what is required without asking the user unless a decision genuinely cannot be inferred.
- Use parallel investigation/execution for genuinely independent work units where the tool surface allows; serialize only dependent branch mutations.
- Before sending ANY user-facing progress/update message, complete at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit.
- Do not artificially stop after one tiny feature, one search, one commit, or one CI submission when useful work remains.
- User-facing messages are substantial checkpoints, not a streaming log of internal activity.

User-facing checkpoint format — IMPORTANT:
- After the 10-minute productive-work threshold, when a checkpoint message is appropriate, keep it short and useful.
- Report exactly four items: (1) what is completed now, (2) any current problem/blocker or explicitly that none exists, (3) where the project stands overall, and (4) exactly ONE next execution step.
- Never send an interim activity log or report a discovery merely because it was found; resolve or integrate it internally first.
- Never invent a blocker.

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

Engineering rules:
- Never redo completed verified work without new evidence.
- Keep SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, and DOCUMENTED distinct.
- TDD first whenever deterministic JVM testing is possible.
- Before adding any feature, inspect current source because older matrix entries can be stale.
- Apply YAGNI; no architecture, dependency, abstraction, or subsystem replacement without demonstrated need.
- WebLibre is a separate project and only a feature/design source pool.
- Do not block engineering on Android runtime availability.
- Do not build/install/test the Android APK after each feature. Continue source/JVM/CI work first.
- Reserve Android build/install/runtime verification for the **final validation phase** as far as reasonably possible; do one consolidated device pass once the planned feature set is mature, collect runtime regressions, fix them together, and rerun final validation as needed.
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
Read the current workflow state and CI status from GitHub. Reconcile the P2.7/P2.8 feature batch result, fix any CI failure internally, and then continue autonomously into the next smallest high-value bounded feature. Do not install the APK yet.

Final-device rule:
The Android device test is the **last major validation step** as far as reasonably possible, not a per-feature loop. Perform source verification, implementation, JVM tests, CI, review, and documentation first. Only after the feature set is sufficiently complete should the agent build/install/run the APK on the phone. Any runtime problems found then should be fixed and the final device validation repeated as required.
```

## Current authoritative checkpoint
- Active branch: `genspark-dev`.
- P2.1–P2.6 are CI-VERIFIED.
- P2.7 media permission guard and P2.8 third-party cookie blocking are source/test implemented; current feature-batch CI must be reconciled from GitHub before status is advanced.
- Android runtime validation is deliberately reserved for the final device pass.
- Continuous autonomous work, the 10-minute minimum before routine user-facing updates, and the concise four-part checkpoint format are mandatory workflow rules.
