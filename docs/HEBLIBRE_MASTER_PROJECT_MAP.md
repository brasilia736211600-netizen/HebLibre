# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Chat history, model memory, plugin memory, and unstated local state are non-authoritative.

## Continuity control plane
The following repository documents are the durable control plane for every human or AI agent:
1. `docs/HEBLIBRE_AI_AGENT_CONTRACT.md` — mandatory operating rules, authority hierarchy, verification model, and handoff requirements.
2. `docs/HEBLIBRE_WORKFLOW_STATE.md` — current executable checkpoint.
3. `docs/HEBLIBRE_EXECUTION_BOARD.md` — current task board, final device checklist, and runtime-defect protocol.
4. `docs/HEBLIBRE_DECISION_LOG.md` — historical decisions, deferred paths, and reasons not to silently reopen them.
5. `docs/HEBLIBRE_RESUME_COMMAND.md` — copy/paste bootstrap for a new chat/session/agent.
6. `docs/HEBLIBRE_WEBLIBRE_GAP_MATRIX.md` — separate WebLibre feature/design comparison only.

## Canonical workflow
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

## Verification ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

Do not substitute one evidence level for another.

## Baseline
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.

## Rules
Continue autonomously on `استمر`; use YAGNI and evidence-based claims. Do not repeatedly build/install APKs. Complete source, deterministic JVM tests, CI, review, and documentation before physical-device validation. WebLibre remains a separate feature/design source pool.

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
- Download cookie privacy control — SOURCE/TEST/CI-VERIFIED, run `33692045747`; main download and Save As paths are both policy-gated.
- BrowserContainer tab reorder core + integration tests — SOURCE/TEST/CI-VERIFIED, run `33692092276`.
- Remote-content default consistency — SOURCE/TEST/CI-VERIFIED, run `33694722442`.
- Whitelist import/export profile-awareness — SOURCE/TEST/CI-VERIFIED; active settings route and legacy `BrowserUnit` helpers use the active normalized profile. Latest source fix `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`; consolidated Unit Tests run `33985143542` passed.
- ARM debug APK packaging and GitHub-hosted Android emulator smoke — runtime/emulator evidence recorded in `HEBLIBRE_WORKFLOW_STATE.md`; this does not replace physical-device verification.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Privacy/architecture boundary
Profile-aware whitelist state and profile identity are implemented, but SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks remain shared/unpartitioned. No multi-process profile isolation, data-directory switching, DoH, proxy/Tor routing, broad fingerprinting engine, extension runtime or on-device AI runtime has been introduced.

## Reader Mode
NOT TARGETED in the current P2 cycle; no bounded dependency-free extraction seam was established.

## QR scanner
Not implemented. No native scanner/decoder, `CAMERA` permission, or ZXing/ML Kit dependency was found. Deferred as MEDIUM.

## PWA
Not implemented. No Web App Manifest parser, install bridge, standalone launch metadata, or service-worker lifecycle integration was found. Deferred as MEDIUM.

## Tab hierarchy
Current model is flat (`List<AlbumController>`) with no parent/opener metadata. Deferred as MEDIUM/architectural work.

## Multi-window
Current `BrowserActivity` uses `singleInstance`; independent browser windows would require lifecycle/state-ownership changes. Deferred as MEDIUM/architectural work.

## Tab stacking / advanced switcher core slice
`TabOrderPolicy` provides deterministic one-step left/right targets with boundary clamping, and `BrowserContainer.move()` reorders an existing tab without destroying WebView state. `BrowserContainerMoveTest` verifies movement and controller identity preservation. SOURCE-VERIFIED, TEST-VERIFIED, and CI-VERIFIED.

The tab overview is a `ScrollView` containing a `LinearLayout`. `AlbumItem` uses normal click for tab selection and long-click for tab removal. Dedicated reorder UI remains deferred so long-click close behavior is preserved.

## Download cookie privacy control
`BrowserUnit.download()` consults `send_download_cookies`; enabled mode forwards a non-empty WebView cookie, disabled mode omits the `Cookie` header. `HelperUnit.save_as()` applies the same policy. Default remains enabled for compatibility.

## Remote-content default consistency
`preference_start.xml` declares `sp_remote` default `true`; `NinjaWebView.loadUrl()` and `NinjaWebView.initPreferences()` use the same fallback.

## Whitelist transfer profile reconciliation
Whitelist import/export in both active and legacy paths resolves `ProfileIdentity` and uses that profile for table reads and duplicate checks. The default profile remains the fallback. Bookmark transfer remains unchanged.

## Security audit decisions
SSL certificate-error override behavior, automatic Android backup of `Ninja4.db`, application-level cleartext traffic, and the coupling of file-origin access with DOM storage under `sp_remote` remain explicit product/architecture decisions. Do not change them opportunistically. See `docs/HEBLIBRE_DECISION_LOG.md`.

## Current phase
`P2 — WebLibre Feature Gap Implementation`

## Current checkpoint
The bounded P2 feature set is complete at the recorded source/test/CI level. Emulator smoke has also completed, but physical target-device verification is still the final release-confidence gate.

## Final validation gate
Before physical testing, verify the live `genspark-dev` HEAD and relevant CI evidence directly from GitHub. Then perform the consolidated target-device pass listed in `docs/HEBLIBRE_EXECUTION_BOARD.md`. Any runtime defect becomes a consolidated fix cycle: reproduce → smallest seam → tests → CI → physical recheck.

## Deferred backlog
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, Reader Mode, and dedicated tab-reorder UI remain deferred by design.

## Last synchronized
2026-09-05 — added the persistent AI-agent continuity control plane and synchronized the master map so a future chat or agent can reconstruct project intent, decisions, evidence, and next actions from GitHub alone.
