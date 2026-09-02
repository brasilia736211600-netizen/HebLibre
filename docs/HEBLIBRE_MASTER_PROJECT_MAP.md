# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active development branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth; chat history and agent memory are non-authoritative.
- Canonical execution protocol: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → COMMIT → SAVE STATE`.
- Canonical handoff state: `docs/HEBLIBRE_WORKFLOW_STATE.md`.
- Canonical resume command: `docs/HEBLIBRE_RESUME_COMMAND.md`.
- Feature gap record: `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

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
- P1 Steps 1–7: profile/identity groundwork and lifecycle/test-seam review complete within documented boundaries.
- P1 Step 8 runtime verification: explicitly deferred because no Android runtime is available.
- P1 Step 8A: created `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` to distinguish existing HebLibre functionality from the separate WebLibre feature pool.
- P2 Step 1: conservative tracking/query-parameter cleanup implemented and CI-VERIFIED.

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
The full comparison remains in `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md`.

### Completed selected gap: P2 Step 1
**Conservative tracking/query-parameter cleanup**.

Implementation:
- `UrlTrackerCleaner` is dependency-free and plain Java.
- TDD tests define removal of `utm_*`, `gclid`, `dclid`, `fbclid`, `msclkid`, and `yclid` while preserving meaningful parameters, path, fragment, and order.
- `BrowserUnit.queryWrapper()` invokes the cleaner only for recognized URL inputs before navigation returns them.
- Search-query generation and Google redirect unwrapping are otherwise unchanged.

Commits:
- `0de51ef47591a1aa9c869bb7909906025662e726` — TDD contract.
- `61795dbb75a22dcaa7a2ed8e6fc3d353358eedc4` — implementation.
- `410b9ba9c8434228c9fac51a10a42e39091c9f35` — raw-component preservation fix.
- `b592f8abb419826caa06b2ec254a299c942d9ac7` — navigation wiring.

CI evidence: run `33648307698`, `test` job successful, including `Run unit tests`.

## Explicit YAGNI boundaries
Do not add without demonstrated need: multi-process architecture, WebView data-directory switching, broad cookie/DOM storage isolation, account systems, broad fingerprinting controls, proxy/Tor stack, WebRTC subsystem, DoH stack, Firefox extension runtime, large AI runtime, unrelated refactors/dependency upgrades, or emulator/instrumentation infrastructure solely for deferred runtime validation.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

P2 Step 1 is complete. Development continues with the next smallest evidence-backed privacy/navigation improvement rather than waiting for Android runtime validation.

## Single next execution target
**P2 Step 2 — source-verify HTTPS-only mode in the existing WebView navigation path. Implement only the smallest local policy seam that the current architecture can support; begin with a deterministic characterization test where possible.**

## Continuity requirements
Every substantive change must update `docs/HEBLIBRE_WORKFLOW_STATE.md` with exact HEAD, change summary, tests, CI/runtime evidence, diff scope, and exactly one next execution step. Update this map when phase/roadmap changes.

## Last synchronized
2026-09-02 — P2 Step 1 completed and CI-VERIFIED; current next target is source verification of HTTPS-only mode.
