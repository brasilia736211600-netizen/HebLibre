# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Canonical workflow: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`.

## Verification ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

## Continuous execution / final-device rules
On `استمر` / `continue`, keep working internally rather than emitting routine updates; use YAGNI and evidence-based claims. Do not repeatedly build/install/test the APK. Source, JVM tests, CI, review, and documentation come first; Android runtime is reserved for the final consolidated device-validation phase.

## Baseline
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.

## Completed engineering
- Build/toolchain recovery, minimal JUnit4 harness, CI workflow recovery.
- P1 profile/identity groundwork and lifecycle/test-seam review; full storage isolation remains unimplemented.
- P2.1 tracking/query cleanup — CI-VERIFIED.
- P2.2 HTTPS-only navigation — CI-VERIFIED.
- P2.3 Global Privacy Control — CI-VERIFIED.
- P2.4 Desktop Mode — CI-VERIFIED.
- P2.5 Screenshot Protection — CI-VERIFIED.
- P2.6 built-in search bangs — CI-VERIFIED.
- P2.7 bounded WebView camera/microphone permission guard — CI-VERIFIED, run `33679583870`.
- P2.8 optional third-party cookie blocking — CI-VERIFIED, run `33679583870`.
- P2.9 geolocation privacy guard — CI-VERIFIED, run `33684710168`.
- P2.10 Save-Data preference contract/fallback correction — CI-VERIFIED, run `33686256788`.

## Save-Data checkpoint
`SaveDataPolicy` has dependency-free JVM coverage and `NinjaWebView.getRequestHeaders()` now falls back to the actual preference default (`true`) instead of `false`; explicit false suppresses `Save-Data: on`.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Profile boundary
Profile-aware whitelist state and profile identity are implemented, but SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain shared/unpartitioned. Full profile isolation is architectural and not claimed complete.

## WebLibre feature-pool direction
Prefer local, dependency-free, deterministic features before architecture-heavy gaps. Do not introduce DoH, proxy/Tor routing, broad fingerprinting defenses, full WebRTC engine changes, extension runtime/uBlock, multi-process/data-directory isolation, or on-device AI without demonstrated need and a separate architectural plan.

## Reader Mode decision
Reader Mode is **NOT TARGETED in the current P2 cycle**. Source tracing found no bounded dependency-free reader-extraction seam in the current native WebView architecture; speculative HTML/JS injection would be invasive and insufficiently deterministic.

## Next candidate
Global settings search is the next candidate. It must first be source-verified as a bounded UI change; if not, select the next smallest high-value local privacy/UX seam.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Current checkpoint
P2.1–P2.10 are CI-VERIFIED. Android runtime is still intentionally deferred. Remote `genspark-dev` HEAD at synchronization time: `02ff0f7bc36b32583deae24f5cdadf5719f6707a`.

## Last synchronized
2026-09-03 — P2.10 CI success reconciled and Reader Mode formally excluded from the current cycle after source trace.
