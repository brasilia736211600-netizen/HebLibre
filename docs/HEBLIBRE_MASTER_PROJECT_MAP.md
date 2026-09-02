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
- P2.2 HTTPS-only — CI-VERIFIED.
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
`Fragment_settings` places a search field above the existing `PreferenceFragmentCompat` list and recursively filters preferences by title/summary using `Preference#setVisible`. `SettingsSearchPolicy` provides the dependency-free deterministic matching contract. Existing preference actions and stored values are not replaced. SOURCE-VERIFIED: complete. TEST-VERIFIED: source test committed; local execution unavailable in this tool surface. CI-VERIFIED: complete, Unit Tests run `33688160810` on feature HEAD `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.

## QR scanner source verification
QR scanning is **not implemented**. Repository inspection found no QR/barcode scanner or decoder, no `CAMERA` permission in `app/src/main/AndroidManifest.xml`, and no ZXing/ML Kit/camera-scanning dependency in `app/build.gradle`. The current WebView camera permission guard is for web-origin media requests and does not provide a native QR capture/decode path. A complete QR scanner would therefore require a new camera/decoder integration and is not a YAGNI/dependency-free bounded change at this checkpoint.

## PWA support source verification
PWA support is **not implemented**. `AndroidManifest.xml` exposes `BrowserActivity` as a conventional `http`/`https` VIEW handler and `BrowserActivity` dispatches incoming VIEW intents into ordinary browser tabs. `NinjaWebViewClient` keeps `http`/`https` navigation inside the WebView and routes non-http schemes externally when possible. No Web App Manifest parsing, install-prompt bridge, PWA install metadata, standalone PWA launch intent, or service-worker lifecycle integration was found. A real installable/standalone PWA feature is therefore MEDIUM and requires a new install/launch lifecycle seam; no bounded dependency-free JVM contract was established. No implementation was made.

## Tab hierarchy source verification
The tab model is flat: `BrowserContainer` stores a `List<AlbumController>` with add/remove/get/index operations, while `BrowserActivity` inserts new tabs relative to the current tab without parent/opener metadata. No parent-child relation, hierarchy identifier, tree traversal, or hierarchy policy exists. Implementing true tab hierarchy would require a new model contract and corresponding tab-creation/UI lifecycle changes, so it is SOURCE-VERIFIED as MEDIUM rather than an immediate YAGNI implementation. No code was changed.

## Tab stacking / advanced switcher core slice
A bounded reorder primitive is now implemented: `TabOrderPolicy` deterministically computes one-step left/right target indices with boundary clamping, and `BrowserContainer.move()` reorders an existing tab without destroying its WebView. This is SOURCE-VERIFIED and TEST-VERIFIED at the source level through committed JUnit tests; CI-VERIFIED is still pending. UI wiring into `BrowserActivity`/tab overview has not been claimed.

## Multi-window source verification
`BrowserActivity` is declared with `android:launchMode="singleInstance"`, so the current design does not provide independent concurrent browser windows. True multi-window support would alter activity/task lifecycle and state ownership and is therefore deferred as MEDIUM/architectural work.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Current checkpoint
P2.1–P2.11 are CI-VERIFIED. QR scanner, PWA support, and tab hierarchy are deferred MEDIUM integrations. Tab reorder has a tested core model operation but still needs UI wiring and CI verification. Android runtime remains deferred.

## Next execution
**Source-verify the smallest remaining bounded download privacy/control seam. Start from `BrowserUnit.download()` cookie-header behavior and existing settings. Do not duplicate download logic or change authenticated-download behavior by default. Add the deterministic test before integration; do not install the APK.**

## Last synchronized
2026-09-03 — tab hierarchy and multi-window were source-verified as non-small; deterministic tab reorder core was added; unintegrated download-cookie experiment was removed; next candidate is bounded download privacy/control work.
