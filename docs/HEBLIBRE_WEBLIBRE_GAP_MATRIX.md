# HebLibre ↔ WebLibre Feature Gap Matrix

## Purpose
Planning artifact comparing current HebLibre with the separate WebLibre feature pool. Current source verification supersedes stale matrix entries.

## Status vocabulary
- **ALREADY** — already present.
- **PARTIAL** — partly present.
- **EASY** — small local change with deterministic JVM coverage.
- **MEDIUM** — several source/UI changes without new platform architecture.
- **ARCHITECTURAL** — new storage/network/engine/process architecture.
- **NOT TARGETED** — intentionally deferred/excluded for the current phase.

## Completed P2 features
| Feature | Status | Evidence |
|---|---|---|
| Tracking/query-parameter cleanup | **COMPLETE / CI-VERIFIED** | Conservative dependency-free cleaner + JVM tests. |
| HTTPS-only mode | **COMPLETE / CI-VERIFIED** | Navigation policy wired into direct/link navigation. |
| Global Privacy Control | **COMPLETE / CI-VERIFIED** | `Sec-GPC: 1` through existing request-header path. |
| Desktop mode | **COMPLETE / CI-VERIFIED** | Deterministic UA policy. |
| Screenshot protection | **COMPLETE / CI-VERIFIED** | Opt-in `FLAG_SECURE`, live preference handling. |
| Search bangs | **COMPLETE / CI-VERIFIED** | Built-in routing for supported engines. |
| WebView camera/microphone permission guard | **COMPLETE / CI-VERIFIED** | `WebRtcPermissionPolicy`; CI run `33679583870`. |
| Third-party cookie blocking | **COMPLETE / CI-VERIFIED** | `ThirdPartyCookiePolicy`; CI run `33679583870`. |
| Geolocation privacy guard | **COMPLETE / CI-VERIFIED** | Canonical preference key; CI run `33684710168`. |
| Save-Data preference contract/fallback | **COMPLETE / CI-VERIFIED** | `SaveDataPolicy` + corrected fallback; CI run `33686256788`. |
| Global settings search | **COMPLETE / CI-VERIFIED** | `SettingsSearchPolicy` + bounded `Fragment_settings` filtering; Unit Tests run `33688160810`. |
| Download cookie control | **COMPLETE / CI-VERIFIED** | `DownloadCookiePolicy` integrated into `BrowserUnit.download()`; CI run `33692045747`. |
| Tab reorder core | **COMPLETE CORE / CI-VERIFIED** | `TabOrderPolicy` + `BrowserContainer.move()` and controller-identity JVM tests; CI run `33692092276`. |
| Remote-content default consistency | **SOURCE-VERIFIED** | `NinjaWebView.initPreferences()` fallback for `sp_remote` aligned with declared preference default and existing navigation fallback; current commit has no CI result established yet. |

## Remaining feature pool
| Feature | HebLibre status | Decision |
|---|---|---|
| Reader Mode | **NOT TARGETED** | Source-traced; no bounded dependency-free reader-extraction seam was established in the native WebView architecture. Do not add speculative HTML/JS injection. |
| QR scanner | **SOURCE-VERIFIED / MEDIUM** | No existing QR/barcode scanner, decoder, camera permission, scanner intent, or QR-specific dependency was found. A complete scanner needs a new camera/decoder integration. Defer until a concrete platform/library decision is justified. |
| PWA support | **SOURCE-VERIFIED / MEDIUM** | Current WebView has no manifest/install/standalone lifecycle seam or deterministic dependency-free contract. Defer until lifecycle scope is explicit. |
| Tab hierarchy | **SOURCE-VERIFIED / MEDIUM** | Current tab model is flat with no parent/opener metadata; true hierarchy requires a new model contract plus UI/lifecycle integration. |
| Tab stacking/advanced switcher UI | **PARTIAL / MEDIUM** | Core reorder is implemented and CI-verified. Dedicated non-long-press UI wiring remains; do not alter long-press close behavior. |
| Container site assignment | MISSING → MEDIUM | Requires container metadata/routing. |
| Container strict/history exclusion | MISSING → MEDIUM | Depends on containers. |
| Tracking Protection engine | PARTIAL → MEDIUM/ARCHITECTURAL | Existing AdBlock; broader engine/filter expansion deferred. |
| DNS over HTTPS | MISSING → ARCHITECTURAL | New resolver/network architecture. |
| Broad fingerprinting defenses | MISSING → ARCHITECTURAL | Engine-level privacy architecture. |
| Full WebRTC engine privacy | PARTIAL → ARCHITECTURAL | Only bounded media-permission guard is implemented. |
| Complete profile storage isolation | PARTIAL → ARCHITECTURAL | Cookies/WebView storage/history/bookmarks remain shared. |
| Isolated tabs | MISSING → ARCHITECTURAL | Storage/process isolation. |
| Per-container proxy/Tor | MISSING → ARCHITECTURAL | Networking architecture. |
| Extensions/uBlock | MISSING → ARCHITECTURAL | Android WebView is not a Firefox extension runtime. |
| On-device AI | MISSING → ARCHITECTURAL | New model/runtime/storage architecture. |
| Translation | MISSING → MEDIUM/ARCHITECTURAL | Service/engine decision. |
| PDF/Markdown/full-page export | PARTIAL | PDF/print exists; Markdown/full-page export remains. |
| Download manager enhancements | PARTIAL | Download handling exists; cookie privacy control is complete; broader manager enhancements remain unscoped. |
| Multi-window | **SOURCE-VERIFIED / MEDIUM** | `BrowserActivity` uses `launchMode="singleInstance"`; true concurrent windows would change task/lifecycle/state ownership. Defer. |

## Selection rule
Prefer the smallest high-value bounded feature with a deterministic seam. Avoid architectural gaps until demonstrated need. Do not install the Android APK during feature development; reserve device testing for the final validation phase.

## Current checkpoint
P2.1–P2.11 are CI-VERIFIED. Download-cookie integration and tab reorder core are CI-VERIFIED. Remote-content default consistency is SOURCE-VERIFIED only. Tab reorder UI remains the next bounded implementation candidate. QR, PWA, hierarchy and multi-window remain deferred. Android runtime remains deferred.

## Last synchronized
2026-09-03 — reconciled current `genspark-dev` source, recorded the download-cookie and reorder-core evidence, and added the remote-content default consistency correction as SOURCE-VERIFIED pending CI evidence.