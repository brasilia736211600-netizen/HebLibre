# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Continuous autonomous-work rule
When the user says `استمر` / `continue`, the agent must keep working internally for as long as useful and must not interrupt the workstream with routine progress messages. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI results, and fix discovered problems autonomously. When a new problem, better priority, or required follow-up is discovered, handle it internally and continue from the new priority instead of sending an interim message. Use parallel investigation/execution for genuinely independent work units where the tool surface permits; serialize only dependent branch mutations. Before sending any user-facing progress/update message, complete at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit. Do not artificially stop after one small feature or one CI submission. The user-facing message is a checkpoint after a substantial work interval, not a streaming log.

## Final Android validation rule
Do not build, install, or repeatedly test the Android APK after each feature. Continue source/JVM/CI work first and reserve Android build/install/runtime verification for the **final validation phase** as far as reasonably possible. Perform one consolidated device validation pass after the planned feature set is mature. Collect all runtime regressions found there, fix them together, then rerun the final device validation as needed. Never block otherwise-ready engineering work merely because device validation is deferred.

## User-facing checkpoint rule
When the minimum 10-minute productive-work threshold has been satisfied and a user-facing update is appropriate, keep the report short and useful. It must state only: (1) what has been completed so far, (2) any current problem/blocker discovered, (3) where the project stands overall, and (4) the single next execution step. Do not stream discoveries, warnings, intermediate CI state, or routine activity before that checkpoint. If no new blocker exists, state that explicitly rather than inventing one.

## Tool-assisted workflow
- **GitHub** is the operational source of truth and primary execution surface.
- **Codex Engineering Guardrails**: apply YAGNI, scope control, verification discipline, and evidence-based claims.
- **Codex Process Jobs**: use only for genuinely independent work units; do not decompose small tasks unnecessarily.
- **Codex Coordinator**: only when multiple workstreams are active or dependencies must be coordinated.
- **CodeRabbit**: use for substantive review when its local CLI/repository surface is available; never substitute it for tests. No CodeRabbit result is claimed in the current environment.
- **Codex Advisor**: only at non-trivial engineering decision points.
- **AI DevKit / Develoop**: optional only when adding concrete capability beyond current tools.
- **Plugin Autopilot**: only for plugin selection/orchestration tasks.
- **Yaps Memory**: convenience only; never continuity authority.
- **Prompt Optimizer**: only when a real prompt-quality bottleneck exists.

Objective: minimum user intervention. `استمر` should resume from GitHub state and advance without unnecessary manual steps.

## Current repository state
- Last verified branch HEAD before the documentation synchronization commits: `e46e37d206320481e024b6783ad8845e3a363c8a`. On every resume, verify the exact remote HEAD directly from GitHub rather than treating this recorded SHA as immutable.
- P2 Steps 1–6 are complete and CI-VERIFIED.
- P2 Step 7 media permission privacy guard and P2 Step 8 third-party cookie privacy control are now **CI-VERIFIED** by Unit Tests run `33679583870` on feature-batch commit `aa5fdace59a746359870a09bfd43644c5e07aeb6`.
- Android runtime verification is intentionally deferred until the feature set is sufficiently complete for a single final device pass.
- Do not build/install/test the APK after each feature. Use JVM tests and GitHub Actions first; perform Android build/install/runtime verification as the final validation phase, then fix any runtime regressions discovered.
- Canonical continuity files: `docs/HEBLIBRE_WORKFLOW_STATE.md`, `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, `docs/HEBLIBRE_RESUME_COMMAND.md`, `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.
2. Minimal JUnit4 harness.
3. CI workflow recovery.
4. P1 profile/identity groundwork; runtime validation deferred.
5. P1 lifecycle/test-seam review.
6. P1 WebLibre/HebLibre feature gap matrix.
7. P2 Step 1: conservative tracking/query-parameter cleanup.
8. P2 Step 2: HTTPS-only navigation.
9. P2 Step 3: Global Privacy Control.
10. P2 Step 4: Desktop Mode.
11. P2 Step 5: Screenshot Protection.
12. P2 Step 6: built-in search bang routing.
13. P2 Step 7: bounded WebView media permission privacy guard — CI-VERIFIED.
14. P2 Step 8: optional third-party cookie blocking — CI-VERIFIED.

## P2 Step 7 — Media permission privacy guard
- Test: `app/src/test/java/de/baumann/browser/unit/WebRtcPermissionPolicyTest.java`.
- Policy: `app/src/main/java/de/baumann/browser/unit/WebRtcPermissionPolicy.java`.
- Preference: `block_media_permissions`, default `true`.
- `NinjaWebChromeClient.onPermissionRequest()` denies camera/microphone resources when enabled.
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: complete.
- CI-VERIFIED: **run `33679583870` — success**.
- ANDROID-RUNTIME-VERIFIED: deferred to final device pass.

## P2 Step 8 — Third-party cookie blocking
- Test: `app/src/test/java/de/baumann/browser/unit/ThirdPartyCookiePolicyTest.java`.
- Policy: `app/src/main/java/de/baumann/browser/unit/ThirdPartyCookiePolicy.java`.
- Preference: `block_third_party_cookies`, default `false` for compatibility.
- `NinjaWebView` applies the policy at initialization, before navigation, and on preference change using `CookieManager.setAcceptThirdPartyCookies()`.
- Project minSdk is 21, matching the guarded API usage.
- SOURCE-VERIFIED: complete.
- TEST-VERIFIED: complete.
- CI-VERIFIED: **run `33679583870` — success**.
- ANDROID-RUNTIME-VERIFIED: deferred to final device pass.

## Existing-feature corrections
- Clear-on-exit is already implemented; do not reimplement.
- OLED/AMOLED pure-black support is already implemented; do not reimplement.
- Desktop Mode is already implemented and must not be selected again as a new feature merely because it appears in an older candidate list.

## Architecture boundary
No multi-process profile isolation, WebView data-directory switching, extension runtime, proxy/Tor stack, DNS-over-HTTPS stack, broad anti-fingerprinting subsystem, or on-device AI runtime has been introduced.

## Next execution step
**Source-trace Reader Mode for a small bounded implementation. If the existing WebView architecture does not expose a safe, dependency-free seam, select the next smallest high-value local privacy/UX feature instead; do not install the APK.**

## Last updated
2026-09-02 — P2.7/P2.8 CI success reconciled and the continuous autonomous-work, final-device, and concise checkpoint-reporting rules are canonical.
