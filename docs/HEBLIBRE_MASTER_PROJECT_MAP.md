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
- P1 profile/identity groundwork; full WebView/storage isolation remains unimplemented.
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
- P2.11 global settings search — CI-VERIFIED, run `33688160810`.
- Download cookie privacy control — SOURCE/TEST/CI-VERIFIED, run `33692045747`.
- BrowserContainer tab reorder core + integration tests — SOURCE/TEST/CI-VERIFIED, run `33692092276`.
- Remote-content default consistency — SOURCE/TEST/CI-VERIFIED, run `33694722442`.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Privacy/architecture boundary
Profile-aware whitelist state and profile identity are implemented, but SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain shared/unpartitioned. No multi-process profile isolation, data-directory switching, DoH, proxy/Tor routing, broad fingerprinting engine, extension runtime or on-device AI runtime has been introduced.

## Reader Mode
NOT TARGETED in the current P2 cycle; no bounded dependency-free extraction seam was established.

## QR scanner
Not implemented. No native scanner/decoder, `CAMERA` permission, or ZXing/ML Kit dependency was found. Deferred as MEDIUM because a real implementation needs camera/decoder integration.

## PWA
Not implemented. No Web App Manifest parser, install bridge, standalone launch metadata, or service-worker lifecycle integration was found. Deferred as MEDIUM.

## Tab hierarchy
Current model is flat (`List<AlbumController>`) with no parent/opener metadata. Deferred as MEDIUM/architectural work.

## Multi-window
Current `BrowserActivity` uses `singleInstance`; independent browser windows would require lifecycle/state-ownership changes. Deferred as MEDIUM/architectural work.

## Tab stacking / advanced switcher core slice
`TabOrderPolicy` provides deterministic one-step left/right targets with boundary clamping, and `BrowserContainer.move()` reorders an existing tab without destroying WebView state. `BrowserContainerMoveTest` verifies left/right movement and controller identity preservation. SOURCE-VERIFIED, TEST-VERIFIED, and CI-VERIFIED via run `33692092276`.

The tab overview is a `ScrollView` containing a `LinearLayout`. `AlbumItem` currently uses normal click for tab selection and long-click for tab removal. Therefore the safe UI follow-up is a separate reorder affordance; long-click must remain close-tab behavior until an explicit replacement contract exists.

## Download cookie privacy control
`BrowserUnit.download()` consults `send_download_cookies`; enabled mode forwards a non-empty WebView cookie, disabled mode omits the `Cookie` header, with the compatibility-preserving default enabled. SOURCE-VERIFIED, TEST-VERIFIED, and CI-VERIFIED via run `33692045747`.

## Remote-content default consistency
`preference_start.xml` declares `sp_remote` default `true`; `NinjaWebView.loadUrl()` and `NinjaWebView.initPreferences()` now use the same default. SOURCE-VERIFIED, TEST/CI-VERIFIED via run `33694722442`.

## Reorder UI reconciliation
The actual source boundary is `BrowserActivity` owning both `BrowserContainer` and `tab_container`, while `AlbumItem` owns the tab view. A temporary controller mutation seam was attempted and immediately reverted because it was incomplete without the corresponding `BrowserActivity` implementation. No incomplete reorder API remains in source. The UI is intentionally still PARTIAL.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Current checkpoint
P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency are CI-VERIFIED. The next bounded candidate remains tab-overview reorder UI, but it must be implemented as a complete source mutation across the actual `BrowserActivity`/`AlbumItem` boundary. QR/PWA/tab hierarchy/multi-window/Reader Mode remain deferred. Android runtime remains deferred.

## Next execution
**Continue parallel source verification on independent bounded privacy/UX seams while keeping tab reorder UI deferred until its complete mutation path can be edited safely. Preserve long-click close behavior. Then run deterministic tests, reconcile CI, review the diff, and synchronize state. Do not install the APK yet.**

## Last synchronized
2026-09-03 — reconciled current branch state, remote-content CI evidence, and the reverted incomplete reorder-controller seam; map synchronized with actual source boundaries.
