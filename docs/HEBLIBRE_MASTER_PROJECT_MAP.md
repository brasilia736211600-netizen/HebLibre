# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth; chat history and agent memory are non-authoritative.
- Canonical execution protocol: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`.
- Canonical handoff state: `docs/HEBLIBRE_WORKFLOW_STATE.md`.
- Canonical resume command: `docs/HEBLIBRE_RESUME_COMMAND.md`.
- Feature gap record: `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

## Tooling roles
- **GitHub**: primary source of truth and execution surface.
- **Codex Engineering Guardrails**: always enforce scope, YAGNI, verification discipline, and evidence-based claims.
- **Codex Process Jobs**: only for genuinely independent parallelizable work.
- **Codex Coordinator**: only when multiple active workstreams need orchestration.
- **CodeRabbit**: substantive diff/PR review and security/code-quality second pass after tests; no CodeRabbit result is claimed when the required local CLI/repository surface is unavailable.
- **Codex Advisor**: non-trivial engineering decisions only.
- **AI DevKit / Develoop**: optional, only when they add a concrete capability beyond the existing toolchain.
- **Plugin Autopilot**: optional orchestration for plugin selection, not part of every code change.
- **Yaps Memory**: convenience only; never a continuity authority.
- **Prompt Optimizer**: optional and event-driven, not a normal pipeline stage.

The intended operating mode is low-intervention: `استمر` should be sufficient to resume and advance work from repository state without unnecessary user prompts.

## Verification ladder
Never collapse these levels:
1. SOURCE-VERIFIED
2. TEST-VERIFIED
3. CI-VERIFIED
4. ANDROID-RUNTIME-VERIFIED
5. DOCUMENTED

## Project baseline
Legacy Android browser/WebView application based on the FOSS Browser codebase. Toolchain baseline: Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for Android SDK tooling in CI.

## Completed engineering sequence
- Build/toolchain recovery: complete; debug build verified locally.
- Minimal JUnit4 harness: complete; `BrowserUnit.isURL` characterization tests.
- CI workflow recovery: complete; JDK 17 for SDK tooling and JDK 11 for Gradle.
- P1 profile/identity groundwork: complete within documented boundary; runtime validation deferred.
- P1 lifecycle/test-seam review: complete.
- P1 WebLibre/HebLibre feature gap matrix: complete and persisted.
- P2 Step 1: conservative tracking/query-parameter cleanup complete.
- P2 Step 2: HTTPS-only navigation policy complete and CI-VERIFIED.
- P2 Step 3: GPC policy, tests, request-header wiring complete and CI-VERIFIED.
- P2 Step 4: Desktop Mode policy, setting, wiring, deterministic JVM tests, and CI verification complete.

## HebLibre original baseline — already implemented
HebLibre already provides substantial browser functionality, including multi-tab browsing/tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation/tool gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with domain whitelists, Safe Browsing, bookmark import/export, and custom User-Agent setting.

These are not migration targets merely because WebLibre also provides them.

## Current profile boundary
### Profile-capable
- In-memory whitelist state: `ProfileScopedWhitelist`.
- Persisted whitelist tables have `PROFILE_ID`.
- Four whitelist classes accept and normalize profile ids.
- Active profile id preference is `current_profile_id`.
- Profile changes use the existing activity restart path.

### Still process/global scoped
- Default SharedPreferences store remains shared.
- `CookieManager` remains process-wide.
- Chromium WebView disk storage remains shared.
- History/bookmarks remain unpartitioned.
- No multi-process profile architecture.

## WebLibre feature-pool decisions
The full comparison remains in `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`. WebLibre is a separate project and is used only as a feature/design source pool.

### P2 Step 1 — tracking/query-parameter cleanup
Conservative dependency-free cleaner. Removes only an explicit allowlist of common analytics/click identifiers plus `utm_*`, while preserving meaningful parameters, path, fragment, and order.

### P2 Step 2 — HTTPS-only navigation policy
`HttpsOnlyPolicy` upgrades absolute `http://` URLs to `https://` on both direct and intercepted-link navigation when enabled. Existing settings UI exposes `https_only`. No HTTP fallback or new networking architecture was introduced. CI run `33650164143` was successful.

### P2 Step 3 — Global Privacy Control
A dependency-free `GpcPolicy` exposes `Sec-GPC: 1` when `gpc_enabled` is true. The existing `NinjaWebView.getRequestHeaders()` path carries the signal for direct navigation, and intercepted HTTP(S) link navigation passes the same headers. Deterministic `GpcPolicyTest` is committed. CI run `33668540065` succeeded for commit `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`.

### P2 Step 4 — Desktop Mode
`DesktopModePolicy` selects a stable desktop user-agent when `desktop_mode` is enabled, otherwise preserving an explicit custom UA or the WebView default UA. The setting is exposed in browser settings and the policy is applied during initialization and immediately before navigation. Deterministic JVM tests pass and Actions run `33677771905` succeeded on feature HEAD `c5c9e77abe8df400bc902099a7877ec6a3d1fc51`.

## Corrected existing-feature findings
- **Clear-on-exit** is already implemented: `sp_clear_quit` exists and `BrowserActivity.onDestroy()` starts `ClearService` when enabled.
- **OLED/AMOLED pure-black theme** already exists as `AppTheme_amoled` with black window/background/navigation colors and white text.

## Explicit YAGNI boundaries
Do not add without demonstrated need: multi-process architecture, WebView data-directory switching, broad cookie/DOM storage isolation, account systems, broad fingerprinting controls, proxy/Tor stack, WebRTC subsystem, DoH stack, Firefox extension runtime, large AI runtime, unrelated refactors/dependency upgrades, or emulator/instrumentation infrastructure solely for deferred runtime validation.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

P2 Steps 1–4 are complete and CI-VERIFIED. Android runtime validation remains deferred.

## Single next execution target
**Source-verify Screenshot Protection as the next high-value bounded feature; confirm there is no existing `FLAG_SECURE`/equivalent path, then implement only the smallest preference + activity-window change that meets the defined product behavior.**

## Continuity requirements
Every substantive change must update `docs/HEBLIBRE_WORKFLOW_STATE.md` with exact HEAD, change summary, tests, CI/runtime evidence, diff scope, and exactly one next execution step. Update this map when the phase/roadmap changes.

## Last synchronized
2026-09-02 — Desktop Mode complete and CI-VERIFIED; Clear-on-exit and AMOLED support source-verified as existing functionality; Screenshot Protection is the next bounded source-verification target.
