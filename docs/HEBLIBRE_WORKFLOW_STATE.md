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
When the user says `استمر` / `continue`, keep working internally for as long as useful and do not interrupt the workstream with routine progress messages. Investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems. Use parallel work only for genuinely independent units; serialize dependent branch mutations. Before a routine user-facing update, complete productive work when tool/runtime conditions permit. Do not stop after one tiny feature or one CI submission while useful work remains.

## Final Android validation rule
Do not build, install, or repeatedly test the APK after each feature. Complete source review, deterministic JVM tests, CI, review, and documentation first. Reserve Android runtime verification for one consolidated final device-validation phase; collect runtime regressions, fix them together, and rerun final validation only as needed.

## Tooling policy
- GitHub is the operational source of truth.
- Apply YAGNI, minimal scope, deterministic tests, and evidence-based claims.
- CodeRabbit is used only when its required local CLI/repository surface is actually available; no CodeRabbit result is claimed here.
- WebLibre is a separate project and only a feature/design source pool.
- Do not introduce architecture, dependencies, or subsystem replacements without demonstrated need.

## Current repository state
- Remote `genspark-dev` HEAD advanced to `64ddb831b061d0e7024bb316344c39a7c41b6339` after download-cookie integration and tab reorder container tests.
- P2.1–P2.11 remain CI-VERIFIED from their recorded successful runs.
- P2.9 Geolocation CI evidence: Unit Tests run `33684710168`, success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data CI evidence: Unit Tests run `33686256788`, success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- P2.11 Global settings search CI evidence: Unit Tests run `33688160810`, success, head `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.
- Download-cookie privacy integration is SOURCE-VERIFIED; Unit Tests run `33692045747` targets its integration commit and was still in progress when this state was saved.
- BrowserContainer reorder integration tests are committed; Unit Tests run `33692092276` targets the test commit and was still in progress when this state was saved.
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
14. P2.11 global settings search — CI-VERIFIED by run `33688160810`.
15. QR scanner source verification — completed; no bounded dependency-free native scanner seam established.
16. PWA support source verification — completed; no bounded manifest/install/standalone lifecycle seam established.
17. Tab hierarchy source verification — completed; current tab model is flat and has no parent-child semantics.
18. Tab reorder core model slice — deterministic `TabOrderPolicy` plus `BrowserContainer.move()` that reorders an existing tab without destroying its WebView state.
19. Download cookie privacy control — added a default-compatible `send_download_cookies` preference, deterministic policy/test contract, and integrated the policy into `BrowserUnit.download()`; disabling it prevents forwarding WebView cookies while the default preserves authenticated-download behavior.
20. BrowserContainer move tests — added JVM-level tests using fake `AlbumController` instances to verify left/right reordering preserves controller identity.

## Tab stacking / advanced switcher core slice
Source review showed indexed insertion was already available, making a small reorder primitive viable. `TabOrderPolicy` deterministically computes one-step left/right target indices with boundary clamping; `BrowserContainer.move()` applies the reorder without destroying the moved WebView. The committed policy and container tests cover movement, boundaries, invalid inputs, and controller identity. SOURCE-VERIFIED: complete. TEST-VERIFIED: source tests committed. CI-VERIFIED: pending on the latest test commit. UI wiring into the tab overview is intentionally not claimed because the current long-press behavior closes tabs and there is no clean non-breaking reorder affordance yet.

## Download privacy seam
`BrowserUnit.download()` now consults `send_download_cookies`, defaulting to enabled for compatibility. When enabled it forwards a non-empty WebView cookie; when disabled it does not add the `Cookie` request header. The preference is exposed in the existing global settings list. This is a bounded control with no new dependency and no change to authenticated-download behavior by default. SOURCE-VERIFIED: complete. TEST-VERIFIED: deterministic policy test committed. CI-VERIFIED: pending run `33692045747` at integration commit `bbbd52ce4ad5590c870f2d740854f037ada42bdc`.

## QR scanner source verification
Repository inspection found no native QR/barcode scanner or decoder, no `CAMERA` permission, and no ZXing/ML Kit/camera-scanning dependency. QR scanning remains MEDIUM and deferred pending a justified camera/decoder decision.

## PWA support source verification
The app has conventional `http`/`https` WebView navigation but no Web App Manifest parser, install prompt bridge, standalone launch metadata, or service-worker lifecycle integration. PWA remains MEDIUM and deferred.

## Tab hierarchy source verification
The tab model is flat with no opener/parent metadata or hierarchy policy. True hierarchy remains MEDIUM and deferred.

## Multi-window source verification
`BrowserActivity` uses `android:launchMode="singleInstance"`; true independent browser windows would alter task/activity lifecycle and state ownership. Deferred as MEDIUM/architectural work.

## Architecture boundary
No multi-process profile isolation, WebView data-directory switching, extension runtime, proxy/Tor stack, DNS-over-HTTPS stack, broad anti-fingerprinting subsystem, or on-device AI runtime has been introduced.

## Reader Mode decision
Reader Mode is NOT TARGETED in the current P2 cycle because no bounded dependency-free extraction seam was established in the native WebView architecture.

## Next execution step
**Continue parallel source verification around the remaining bounded UX/privacy seams. First reconcile Unit Tests runs `33692045747` and `33692092276`; if they pass, mark download-cookie control and tab reorder core TEST/CI evidence accordingly. Then inspect the existing tab-overview UI for the smallest non-breaking reorder affordance. Keep QR/PWA/tab hierarchy/multi-window deferred and do not install the APK.**

## Last updated
2026-09-03 — download-cookie control integrated, BrowserContainer move tests added, and CI reconciliation is pending on runs `33692045747` and `33692092276`.
