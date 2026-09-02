# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Canonical workflow: `READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`.

## Verification ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

## Baseline
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.

## Rules
Continue autonomously on `استمر`; apply YAGNI and evidence-based claims. Do not repeatedly build/install APKs. Source, JVM tests, CI, review and docs come first; Android runtime is reserved for final consolidated device validation. WebLibre is a separate feature/design source pool.

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
- P2.11 global settings search — CI-VERIFIED by Unit Tests run `33688160810`.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Privacy/architecture boundary
Profile-aware whitelist state and profile identity are implemented, but SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain shared/unpartitioned. No multi-process profile isolation, data-directory switching, DoH, proxy/Tor routing, broad fingerprinting engine, extension runtime or on-device AI runtime has been introduced.

## Reader Mode decision
Reader Mode is **NOT TARGETED in the current P2 cycle**. Source tracing found no bounded dependency-free reader-extraction seam; speculative HTML/JS injection is intentionally excluded.

## P2.11 — Global settings search
`Fragment_settings` places a search field above the existing `PreferenceFragmentCompat` list and recursively filters preferences by title/summary using `Preference#setVisible`. `SettingsSearchPolicy` provides the dependency-free deterministic matching contract. Existing preference actions and stored values are not replaced. SOURCE-VERIFIED: complete. TEST-VERIFIED: source test committed; local execution unavailable in this tool surface. CI-VERIFIED: complete, Unit Tests run `33688160810` on HEAD `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Current checkpoint
P2.1–P2.11 are CI-VERIFIED. Android runtime remains deferred.

## Next execution
**Source-verify the next smallest high-value bounded privacy/UX seam from the gap matrix. Prefer a local, dependency-free change with deterministic JVM coverage. Do not install the APK.**

## Last synchronized
2026-09-03 — P2.11 CI reconciled successfully; checkpoint advanced.
