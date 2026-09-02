# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Canonical workflow: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`.
- Continuity files: `docs/HEBLIBRE_WORKFLOW_STATE.md`, `docs/HEBLIBRE_MASTER_PROJECT_MAP.md`, `docs/HEBLIBRE_RESUME_COMMAND.md`, `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

## Continuous autonomous-work rule
When the user says `استمر` / `continue`, work continuously rather than emitting routine progress updates. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems internally; do not interrupt the workstream merely to report a discovery or issue. Use parallel investigation/execution for genuinely independent work units where possible and serialize dependent branch mutations. Before any user-facing progress/update message, complete at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit. Do not artificially stop after one small feature, one search, one commit, or one CI submission when useful work remains.

## Final Android-validation rule
Do not build/install/test the APK after every feature. Accumulate source verification, deterministic JVM tests, CI verification, and bounded feature work first. Reserve Android build/install/runtime verification for one consolidated final device-validation phase as far as reasonably possible. During that final pass, collect runtime regressions, fix them together, and rerun device validation only as necessary.

## User-facing checkpoint rule
User-facing updates are checkpoints, not an activity stream. After the 10-minute productive-work threshold, keep any update short and useful: state what is completed now, any current problem/blocker, where the project stands overall, and exactly one next step. Do not send interim messages for discoveries, warnings, failing tests, CI transitions, or priority changes; resolve or integrate those internally first. Never invent a blocker when none exists.

## Tooling policy
GitHub is primary. Codex Engineering Guardrails enforce YAGNI and evidence-based verification. Process Jobs and Coordinator are used only for genuinely independent/parallel work. CodeRabbit is a second-pass review when its required local CLI/repository surface is available; no CodeRabbit result is claimed in the current environment. Advisor is for non-trivial decisions. AI DevKit/Develoop and Plugin Autopilot are optional capability-specific tools. Yaps Memory is non-authoritative. Prompt Optimizer is event-driven only.

## Verification ladder
SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED. Do not conflate levels.

## Baseline
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for Android SDK tooling in CI.

## Completed engineering
- Build/toolchain recovery, minimal JUnit4 harness, and CI workflow.
- P1 profile/identity groundwork and lifecycle/test-seam review; runtime validation deferred.
- P1 WebLibre/HebLibre feature gap matrix.
- P2.1 tracking/query cleanup — CI-VERIFIED.
- P2.2 HTTPS-only navigation — CI-VERIFIED.
- P2.3 GPC — CI-VERIFIED.
- P2.4 Desktop Mode — CI-VERIFIED.
- P2.5 Screenshot Protection — CI-VERIFIED.
- P2.6 built-in search bang routing — CI-VERIFIED.
- P2.7 bounded WebView media permission privacy guard — **CI-VERIFIED, run `33679583870`**.
- P2.8 optional third-party cookie blocking — **CI-VERIFIED, run `33679583870`**.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Profile boundary
Profile-aware whitelist state and profile identity are implemented, but SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain shared/unpartitioned. Full profile isolation is not claimed.

## Privacy controls just added
### Media permissions
`WebRtcPermissionPolicy` blocks WebView camera/microphone capture resources when `block_media_permissions` is enabled. `NinjaWebChromeClient.onPermissionRequest()` enforces the guard. This is intentionally narrower than full WebRTC engine privacy control. Default is enabled.

### Third-party cookies
`ThirdPartyCookiePolicy` controls `CookieManager.setAcceptThirdPartyCookies()` through `block_third_party_cookies`. `NinjaWebView` applies it at initialization, before navigation, and live on preference changes. Default is off for compatibility.

## WebLibre feature-pool direction
Prefer local, dependency-free, deterministic features before architectural gaps. Architectural items remain deferred: multi-process/data-directory isolation, DoH, proxy/Tor routing, broad fingerprinting, full WebRTC engine changes, extension runtime/uBlock, large AI runtime, and similar subsystem replacements.

## Next candidate
Reader Mode remains only a candidate and must be source-verified before implementation. If it is not a small bounded seam, choose the next high-value local privacy/UX feature instead. Never reselect Desktop Mode or another already-present feature.

## Current checkpoint
P2.7/P2.8 feature work is CI-VERIFIED by Unit Tests run `33679583870` on feature-batch commit `aa5fdace59a746359870a09bfd43644c5e07aeb6`. Later documentation commits do not change that feature evidence. Android runtime remains reserved for final validation.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Last synchronized
2026-09-02 — P2.7/P2.8 CI success reconciled; autonomous execution, final-device validation, and concise checkpoint-reporting rules remain mandatory.
