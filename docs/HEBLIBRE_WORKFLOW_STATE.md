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
- Remote `genspark-dev` HEAD verified directly from GitHub: `2a523ff8069aa1fe81ff87d91733c0f00f8698c0`.
- P2.1–P2.11 are CI-VERIFIED.
- P2.9 Geolocation CI evidence: Unit Tests run `33684710168`, success, head `e48c1f036aa4c7fcaea7339735c7fe81201c5d9d`.
- P2.10 Save-Data CI evidence: Unit Tests run `33686256788`, success, head `1a71cd2eb358bfd57f3209d141fc40253183dff1`.
- P2.11 Global settings search CI evidence: Unit Tests run `33688160810`, success, head `7cc68ab9eae51faea830f94bd9381fdc880b68e4`.
- Tab reorder core slice is SOURCE-VERIFIED and TEST-VERIFIED at source level; CI is pending for the new commits.
- A download-cookie policy experiment was rejected and removed because the current `BrowserUnit.download()` path would require a direct integration edit; no dead policy/test code remains.
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

## Tab stacking / advanced switcher core slice
Source review showed indexed insertion was already available, making a small reorder primitive viable. `TabOrderPolicy` deterministically computes one-step left/right target indices with boundary clamping; `BrowserContainer.move()` applies the reorder without destroying the moved WebView. The committed `TabOrderPolicyTest` covers movement, boundaries, and invalid inputs. SOURCE-VERIFIED: complete. TEST-VERIFIED: source tests committed; local execution unavailable. CI-VERIFIED: pending. UI wiring into the tab overview is intentionally not claimed.

## Download privacy seam decision
`BrowserUnit.download()` currently obtains the WebView cookie for the URL and unconditionally forwards it as a `Cookie` request header to `DownloadManager`. The repository has no existing download-cookie preference. A standalone policy class/test without wiring would be dead code, while direct integration requires modifying the large `BrowserUnit` method. Under YAGNI, the experiment was removed rather than merged. The existing authenticated-download behavior remains unchanged.

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
**Continue parallel source verification around remaining bounded UX/privacy seams. Prioritize tab overview/advanced switcher UI wiring only if a deterministic, low-risk integration point exists; otherwise inspect another small existing-control seam. Keep download-cookie changes deferred unless a clean preference integration point is identified. Do not install the APK.**

## Last updated
2026-09-03 — current branch HEAD synchronized to `2a523ff8069aa1fe81ff87d91733c0f00f8698c0`; unintegrated download-cookie experiment removed; tab reorder core retained; final Android validation remains deferred.
