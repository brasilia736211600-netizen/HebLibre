# HebLibre ↔ WebLibre Feature Gap Matrix

## Purpose
This document is a planning artifact only. It compares the existing HebLibre baseline with the feature pool from the separate personal WebLibre project and upstream WebLibre work. It does **not** claim that every WebLibre feature should be ported.

GitHub remains the source of truth. Existing HebLibre functionality must not be reimplemented merely because the same capability exists in WebLibre.

## Status vocabulary
- **ALREADY** — HebLibre already provides the capability in its current architecture.
- **PARTIAL** — HebLibre has part of the capability, but the WebLibre-level behavior is broader.
- **EASY** — can be added with a small, local, dependency-free change and deterministic JVM coverage.
- **MEDIUM** — requires several coordinated source/UI changes but no new platform architecture.
- **ARCHITECTURAL** — would require a new storage, networking, engine, or process architecture and is not a near-term target under YAGNI.
- **NOT TARGETED** — intentionally excluded until a demonstrated product need exists.

## HebLibre baseline already present
The current HebLibre source already contains, among other things:
- multi-tab browsing through an instance-scoped `BrowserContainer`;
- tab preview/overview;
- Home, Bookmarks, and History;
- search/autocomplete and configurable search engines;
- navigation/tool gestures;
- find-in-page;
- PDF/print flow;
- downloads/download handling;
- fullscreen/video-related handling;
- JavaScript, Cookie, Remote, and AdBlock controls with domain whitelists;
- Safe Browsing;
- bookmark import/export;
- custom User-Agent setting.

These are treated as **ALREADY**, not migration targets.

## Gap matrix — high-value WebLibre pool

| Feature from WebLibre pool | HebLibre status | Scope / decision |
|---|---|---|
| Tracking/query-parameter cleanup | **COMPLETE / EASY** | Implemented as conservative dependency-free URL cleanup with JVM coverage. |
| Desktop mode | **COMPLETE / CI-VERIFIED** | Added a desktop-mode preference and stable desktop user-agent policy, preserving custom UA behavior when desktop mode is off. CI run `33677771905` succeeded for the desktop-mode feature HEAD `c5c9e77...`. |
| Reader Mode | **MISSING → MEDIUM** | Requires content extraction/rendering path; larger than URL cleanup. |
| QR scanner | **MISSING → MEDIUM** | Camera/scan UI and dependency decision required. Not first target. |
| PWA support | **MISSING → MEDIUM** | Requires install/launch lifecycle and manifest handling. |
| Local full-text search | **MISSING → ARCHITECTURAL** | Requires an index and lifecycle/storage design. Not first target. |
| Tab hierarchy / parent-child tree | **PARTIAL → MEDIUM** | Existing tab container exists, but hierarchy semantics and UI are absent. |
| Tab stacking / advanced switcher views | **PARTIAL → MEDIUM** | Existing tab overview is present; stacking/accordion/two-row behavior is not. |
| Isolated tabs | **MISSING → ARCHITECTURAL** | True isolation requires WebView/storage architecture beyond current process-global Chromium storage. |
| Multi-profile complete storage separation | **PARTIAL → ARCHITECTURAL** | Current P1 only isolates profile-aware whitelist state; CookieManager/WebView storage/history/bookmarks remain shared. |
| Container site assignment | **MISSING → MEDIUM** | Requires container metadata and navigation routing model. |
| Container strict mode/history exclusion | **MISSING → MEDIUM** | Builds on container metadata and history routing; not needed before basic containers exist. |
| Per-container proxy/Tor routing | **MISSING → ARCHITECTURAL** | Requires proxy/Tor networking architecture; outside immediate YAGNI boundary. |
| Tracking Protection engine / larger filter DB | **PARTIAL → MEDIUM/ARCHITECTURAL** | Basic AdBlock exists; WebLibre-grade tracking protection requires engine/filter-data expansion. |
| HTTPS-only mode | **COMPLETE / CI-VERIFIED** | Implemented in direct and intercepted-link navigation with deterministic JVM tests and CI verification. |
| DNS over HTTPS | **MISSING → ARCHITECTURAL** | Requires resolver/network integration not present in WebView architecture. |
| Global Privacy Control | **COMPLETE / CI-VERIFIED** | `Sec-GPC: 1` is implemented through the existing request-header path with a dependency-free policy, JVM contract tests, and successful CI run `33668540065`. |
| Fingerprinting defenses | **MISSING → ARCHITECTURAL** | Broad anti-fingerprinting changes are not justified before foundational navigation/privacy work. |
| WebRTC privacy controls | **MISSING → ARCHITECTURAL** | Requires engine-level handling not exposed by current architecture. |
| Screenshot protection | **PARTIAL/VERIFY** | Existing screenshot/fullscreen-related handling exists; exact prevention semantics need source verification before claiming parity. |
| Clear-on-exit | **ALREADY / SOURCE-VERIFIED** | Existing `sp_clear_quit` preference and `BrowserActivity.onDestroy()` → `ClearService` path already implement clear-on-exit. Do not reimplement. |
| Extensions | **MISSING → ARCHITECTURAL** | Current Android WebView architecture is not a Firefox-extension runtime. Not a near-term port. |
| uBlock Origin | **MISSING → ARCHITECTURAL** | Depends on extension/engine capabilities absent here. Existing AdBlock remains separate. |
| On-device AI | **MISSING → ARCHITECTURAL** | Requires model/runtime/storage/UI architecture. Not a near-term target. |
| Translation | **MISSING → MEDIUM/ARCHITECTURAL** | Requires translation service/engine choice. Not first target. |
| PDF/Markdown/full-page export | **PARTIAL** | PDF/print already exists; Markdown/full-page export is additional functionality. |
| Custom search engines / bangs | **PARTIAL** | Configurable search engines exist; WebLibre-style bang routing is additional. |
| Download manager enhancements | **PARTIAL** | Download handling exists; external manager/advanced controls are additional. |
| Multi-window | **PARTIAL/VERIFY** | Some Android/system integration exists, but full WebLibre-style multi-window behavior needs explicit source verification. |
| Tab gestures | **ALREADY** | Gesture infrastructure already exists in HebLibre. |
| OLED/pure-black theme | **ALREADY / SOURCE-VERIFIED** | `AppTheme_amoled` already exists with black window/background/navigation colors and white primary/secondary text. Do not reimplement. |
| Global settings search | **MISSING → EASY/MEDIUM** | UI-only indexing/search over existing preferences; useful but lower priority than navigation/privacy. |
| Container/backup migration | **MISSING → MEDIUM/ARCHITECTURAL** | Depends on actual container/profile data model. |

## Completed P2 targets
### P2 Step 1 — tracking/query-parameter cleanup
Conservative dependency-free cleaner with JVM coverage.

### P2 Step 2 — HTTPS-only navigation
`HttpsOnlyPolicy` upgrades absolute `http://` URLs to `https://` on direct and intercepted-link navigation when enabled. CI-VERIFIED.

### P2 Step 3 — Global Privacy Control
`GpcPolicy` exposes `Sec-GPC: 1` when `gpc_enabled` is true through the existing WebView request-header path. Deterministic JVM tests and CI verification are complete; CI run `33668540065` succeeded for commit `e96298cbb2c0d0f3d9813ddc913bcbb98b348e2f`.

### P2 Step 4 — Desktop mode (COMPLETE)
A dependency-free `DesktopModePolicy` selects a stable desktop user-agent when `desktop_mode` is enabled, otherwise preserving a user-supplied custom UA or the WebView default UA. The preference is exposed in browser settings and applied before each navigation. Deterministic JVM tests are committed and CI run `33677771905` completed successfully on feature HEAD `c5c9e77abe8df400bc902099a7877ec6a3d1fc51`.

## P2 Step 4 selection rule
Prefer the smallest high-value feature that is local, dependency-free, and has a deterministic JVM seam. Avoid architectural gaps until there is a demonstrated product need. Do not reimplement anything already present in HebLibre.

## Explicit YAGNI boundaries
Do not add without demonstrated need: multi-process architecture, WebView data-directory switching, broad cookie/DOM storage isolation, account systems, broad fingerprinting controls, proxy/Tor stack, WebRTC subsystem, DoH stack, Firefox extension runtime, large AI runtime, unrelated refactors/dependency upgrades, or emulator/instrumentation infrastructure solely for deferred runtime validation.

## Verification boundary
This matrix is **DOCUMENTED / SOURCE-INFORMED**. Individual completed targets carry their own verification records in `docs/HEBLIBRE_WORKFLOW_STATE.md`.

## Last synchronized
2026-09-02 — P2 Step 4 Desktop Mode complete and CI-VERIFIED; Clear-on-exit and AMOLED/pure-black support independently confirmed as existing HebLibre functionality.
