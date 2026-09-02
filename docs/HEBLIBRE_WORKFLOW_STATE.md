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
- Active branch `genspark-dev` HEAD: `71558fc7aca3fd86e683bd1d59ad1eb618bf5135` at this checkpoint.
- P2.1–P2.11 remain CI-VERIFIED from their recorded successful runs.
- P2.9 Geolocation CI evidence: Unit Tests run `33684710168`, success.
- P2.10 Save-Data CI evidence: Unit Tests run `33686256788`, success.
- P2.11 Global settings search CI evidence: Unit Tests run `33688160810`, success.
- Download-cookie integration CI run `33692045747`: completed success; unit-test job completed success.
- BrowserContainer reorder integration-test CI run `33692092276`: completed success; unit-test job completed success.
- Remote-content default consistency correction CI run `33694722442`: completed success; unit-test job completed success.
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
19. Download cookie privacy control — added a default-compatible `send_download_cookies` preference, deterministic policy/test contract, and integrated the policy into `BrowserUnit.download()`.
20. BrowserContainer move tests — added JVM-level tests using fake `AlbumController` instances to verify left/right reordering preserves controller identity.
21. Remote-content default consistency — aligned `sp_remote` fallback behavior with the declared preference default and existing navigation fallback; CI-VERIFIED by run `33694722442`.

## Tab stacking / advanced switcher core slice
Source review showed indexed insertion was already available, making a small reorder primitive viable. `TabOrderPolicy` deterministically computes one-step left/right target indices with boundary clamping; `BrowserContainer.move()` applies the reorder without destroying the moved WebView. The committed policy and container tests cover movement, boundaries, invalid inputs, and controller identity. SOURCE-VERIFIED: complete. TEST-VERIFIED: complete. CI-VERIFIED: complete via run `33692092276`. UI wiring remains deferred: `dialog_overview.xml` uses a `ScrollView` containing a `LinearLayout`, while `AlbumItem` maps normal click to selection and long-click to tab removal. A reorder control must be added without stealing the existing long-click close contract.

## Download privacy seam
`BrowserUnit.download()` consults `send_download_cookies`, defaulting to enabled for compatibility. When enabled it forwards a non-empty WebView cookie; when disabled it omits the `Cookie` request header. SOURCE-VERIFIED: complete. TEST-VERIFIED: complete. CI-VERIFIED: complete via run `33692045747`.

## Remote-content default seam
`preference_start.xml` declares `sp_remote` default `true`; `NinjaWebView.loadUrl()` already used `true`; `NinjaWebView.initPreferences()` was corrected from `false` to `true`. Direct Actions evidence for Unit Tests run `33694722442` shows the test job and unit-test step completed successfully. SOURCE-VERIFIED, TEST/CI-VERIFIED, and DOCUMENTED.

## Reorder UI seam reconciliation
A direct source trace found the actual mutation boundary: `BrowserActivity` owns the `BrowserContainer` and `tab_container`, while `AlbumItem` owns each tab view and currently exposes normal-click selection plus long-click removal. A temporary `BrowserController.moveAlbum(...)` seam was tested conceptually but immediately reverted because it was incomplete without the corresponding `BrowserActivity` mutation path. The revert left the source tree without that incomplete API. Therefore the UI remains intentionally PARTIAL rather than being falsely marked implemented.

## Remaining bounded-source decisions
QR scanner, PWA support, tab hierarchy, multi-window, and Reader Mode remain deferred because no smaller dependency-free seam has been established. Continue source verification on genuinely independent privacy/UX seams rather than introducing speculative architecture.

## Architecture boundary
No multi-process profile isolation, WebView data-directory switching, extension runtime, proxy/Tor stack, DNS-over-HTTPS stack, broad anti-fingerprinting subsystem, or on-device AI runtime has been introduced.

## Next execution step
**Continue parallel source verification on independent bounded privacy/UX seams while keeping tab reorder UI deferred until a complete non-breaking mutation path can be edited safely. Preserve long-click close behavior. Keep deferred architectural features deferred and do not install the APK yet.**

## Last updated
2026-09-03 — reconciled current branch HEAD, remote-content CI evidence, and the temporary/reverted reorder-controller seam; documentation remains synchronized with the actual source boundary.
