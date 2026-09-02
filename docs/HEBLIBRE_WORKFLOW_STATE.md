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
When the user says `استمر` / `continue`, keep working internally for as long as useful and do not interrupt the workstream with routine progress messages. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems. Use parallel work only for genuinely independent units; serialize dependent branch mutations. Before a routine user-facing update, complete at least 10 minutes of productive work when tool/runtime conditions permit. Do not stop after one tiny feature or one CI submission while useful work remains.

## Final Android validation rule
Do not build, install, or repeatedly test the APK after each feature. Complete source review, deterministic JVM tests, CI, review, and documentation first. Reserve Android build/install/runtime verification for one consolidated final device-validation phase; collect runtime regressions, fix them together, and rerun final validation only as needed.

## Tooling policy
- GitHub is the operational source of truth.
- Apply YAGNI, minimal scope, deterministic tests, and evidence-based claims.
- CodeRabbit is used only when its required local CLI/repository surface is actually available; no CodeRabbit result is claimed here.
- WebLibre is a separate project and only a feature/design source pool.
- Do not introduce architecture, dependencies, or subsystem replacements without demonstrated need.

## Current repository state
- Exact remote branch HEAD: `fa1eef7d6579c1a26d8719aefd681a1cd1b266e4` was the implementation commit when this state update began; subsequent commits are documentation/resource synchronization. Verify exact HEAD on every resume.
- P2.1–P2.10 are CI-VERIFIED.
- P2.9 Geolocation CI evidence: Unit Tests run `33684710168`, success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data CI evidence: Unit Tests run `33686256788`, success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- Android runtime verification remains intentionally deferred to the final device pass.

## Completed engineering
1. Build/toolchain recovery: Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.
2. Minimal JUnit4 harness and CI workflow recovery.
3. P1 profile/identity groundwork and lifecycle/test-seam review; full profile storage isolation remains unimplemented.
4. P2.1 tracking/query-parameter cleanup — CI-VERIFIED.
5. P2.2 HTTPS-only navigation — CI-VERIFIED.
6. P2.3 Global Privacy Control — CI-VERIFIED.
7. P2.4 Desktop Mode — CI-VERIFIED.
8. P2.5 Screenshot Protection — CI-VERIFIED.
9. P2.6 built-in search bang routing — CI-VERIFIED.
10. P2.7 bounded WebView media permission privacy guard — CI-VERIFIED by run `33679583870`.
11. P2.8 optional third-party cookie blocking — CI-VERIFIED by run `33679583870`.
12. P2.9 geolocation privacy guard — CI-VERIFIED by run `33684710168`.
13. P2.10 Save-Data preference contract/fallback correction — CI-VERIFIED by run `33686256788`.
14. P2.11 global settings search — implementation committed; deterministic JVM contract added. CI verification pending.

## P2.9 — Geolocation privacy guard
`GeolocationPermissionPolicy` and its JVM tests are complete. `NinjaWebChromeClient.onGeolocationPermissionsShowPrompt()` reads the canonical geolocation preference key; disabled invokes `callback.invoke(origin, false, false)` and enabled preserves the existing runtime-permission flow. Android runtime verification is deferred.

## P2.10 — Save-Data
`SaveDataPolicy` and JVM tests define the preference contract. `NinjaWebView.getRequestHeaders()` now uses `SaveDataPolicy.DEFAULT_ENABLED` as the fallback, matching the actual `preference_start.xml` default of true. Explicit false still suppresses `Save-Data: on`. CI run `33686256788` is successful.

## P2.11 — Global settings search
A source-verified bounded UI seam exists in `Fragment_settings`: a search field is added above the existing `PreferenceFragmentCompat` list, and recursive visibility filtering searches preference title and summary without changing stored values or navigation wiring. `SettingsSearchPolicy` provides the dependency-free deterministic matching contract with JVM tests. SOURCE-VERIFIED: complete. TEST-VERIFIED: not yet locally executed. CI-VERIFIED: pending.

## Existing-feature corrections
- Clear-on-exit is already implemented; do not reimplement.
- AMOLED/pure-black support is already implemented; do not reimplement.
- Desktop Mode is already implemented; do not reselect it.

## Architecture boundary
No multi-process profile isolation, WebView data-directory switching, extension runtime, proxy/Tor stack, DNS-over-HTTPS stack, broad anti-fingerprinting subsystem, or on-device AI runtime has been introduced.

## Reader Mode decision
Reader Mode was source-traced against the current native WebView architecture. No bounded dependency-free `evaluateJavascript`/reader-extraction seam was established; ad-hoc HTML/JS extraction would be invasive and insufficiently deterministic. Reader Mode is therefore NOT TARGETED in the current P2 cycle.

## Next execution step
**Reconcile CI for P2.11 global settings search. If CI fails, fix the smallest concrete issue and rerun; if CI succeeds, record it and advance to the next bounded privacy/UX seam. Do not install the APK.**

## Last updated
2026-09-03 — P2.11 settings-search implementation recorded; CI still pending.
