# HebLibre ↔ WebLibre Feature Gap Matrix

## Purpose
Planning artifact comparing current HebLibre with the separate WebLibre feature pool. It does not imply every WebLibre feature should be ported. Current source verification supersedes stale matrix entries.

## Status vocabulary
- **ALREADY** — already present in HebLibre.
- **PARTIAL** — partly present; broader behavior remains.
- **EASY** — small local change with deterministic JVM coverage.
- **MEDIUM** — several source/UI changes without new platform architecture.
- **ARCHITECTURAL** — requires new storage/network/engine/process architecture.
- **NOT TARGETED** — intentionally deferred.

## Continuous execution rule
When continuing work, do not interrupt the engineering stream with routine progress messages. Discoveries, warnings, test failures, CI failures, priority changes, and follow-up fixes are handled internally before the next user-facing checkpoint. Use parallel investigation/execution for genuinely independent work units where possible; serialize dependent branch mutations. A user-facing progress/update message should be sent only after at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit.

## Final Android-validation rule
Do not build, install, or repeatedly test the Android APK after each feature. Continue source verification, deterministic JVM tests, CI verification, review, and bounded implementation first. Reserve Android build/install/runtime verification for one consolidated final validation phase as far as reasonably possible. Fix regressions discovered in that final pass together and rerun device validation only as necessary.

## Baseline already present
Multi-tab browsing/tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present. Do not reimplement them merely because WebLibre also provides them.

## Feature pool
| Feature | HebLibre status | Decision |
|---|---|---|
| Tracking/query-parameter cleanup | **COMPLETE / CI-VERIFIED** | Conservative dependency-free cleaner with JVM tests. |
| HTTPS-only mode | **COMPLETE / CI-VERIFIED** | Direct and link navigation policy. |
| Global Privacy Control | **COMPLETE / CI-VERIFIED** | `Sec-GPC: 1` through existing request-header path. |
| Desktop mode | **COMPLETE / CI-VERIFIED** | Stable desktop UA policy; custom UA preserved when off. |
| Screenshot protection | **COMPLETE / CI-VERIFIED** | Opt-in `FLAG_SECURE` with live preference handling. |
| Search bangs | **COMPLETE / CI-VERIFIED** | Built-in routing for supported search engines. |
| WebView camera/microphone permission guard | **IMPLEMENTED / CI-PENDING** | `WebRtcPermissionPolicy` + `NinjaWebChromeClient.onPermissionRequest()`; blocks camera/microphone capture when `block_media_permissions` is enabled. Full WebRTC engine privacy remains architectural. |
| Third-party cookie blocking | **IMPLEMENTED / CI-PENDING** | `ThirdPartyCookiePolicy` + `CookieManager.setAcceptThirdPartyCookies()`; opt-in setting, compatibility default off. |
| Reader Mode | **MISSING → MEDIUM** | Next candidate only after source-verifying a bounded implementation. |
| QR scanner | **MISSING → MEDIUM** | Camera/scan UI and dependency decision. Lower priority than current privacy work. |
| PWA support | **MISSING → MEDIUM** | Install/launch lifecycle and manifest handling. |
| Tab hierarchy | **PARTIAL → MEDIUM** | Existing tabs, no parent-child model. |
| Tab stacking/advanced switcher | **PARTIAL → MEDIUM** | Existing overview, no stacking semantics. |
| Container site assignment | **MISSING → MEDIUM** | Requires container metadata/routing. |
| Container strict/history exclusion | **MISSING → MEDIUM** | Depends on containers. |
| Tracking Protection engine | **PARTIAL → MEDIUM/ARCHITECTURAL** | Existing AdBlock; broader engine/filter expansion deferred. |
| DNS over HTTPS | **MISSING → ARCHITECTURAL** | New resolver/network architecture required. |
| Broad fingerprinting defenses | **MISSING → ARCHITECTURAL** | Engine-level privacy architecture required. |
| Full WebRTC engine privacy | **PARTIAL → ARCHITECTURAL** | Only bounded media permission guard is implemented. |
| Complete profile storage isolation | **PARTIAL → ARCHITECTURAL** | CookieManager/WebView storage/history/bookmarks remain shared. |
| Isolated tabs | **MISSING → ARCHITECTURAL** | Storage/process isolation required. |
| Per-container proxy/Tor | **MISSING → ARCHITECTURAL** | Networking architecture required. |
| Extensions/uBlock | **MISSING → ARCHITECTURAL** | Current Android WebView is not a Firefox extension runtime. |
| On-device AI | **MISSING → ARCHITECTURAL** | New model/runtime/storage architecture. |
| Translation | **MISSING → MEDIUM/ARCHITECTURAL** | Service/engine decision required. |
| PDF/Markdown/full-page export | **PARTIAL** | PDF/print exists; Markdown/full-page export remains. |
| Download manager enhancements | **PARTIAL** | Download handling exists; advanced controls remain. |
| Multi-window | **PARTIAL/VERIFY** | Needs explicit source verification before parity claims. |
| Global settings search | **MISSING → EASY/MEDIUM** | Useful but lower priority than privacy/navigation. |

## Selection rule
Prefer the smallest high-value bounded feature with a deterministic seam. Avoid architectural gaps until demonstrated need. Do not install the Android APK during feature development; reserve device testing for the final validation phase.

## Current checkpoint
P2 Steps 1–6 are CI-VERIFIED. P2.7 media permission guard and P2.8 third-party cookie blocking are source/test implemented. Their feature batch at `aa5fdace59a746359870a09bfd43644c5e07aeb6` is under CI verification; subsequent commits are documentation/workflow checkpoints.

## Next candidate after CI
Prefer **Reader Mode** only after source-verifying a small bounded implementation. Otherwise choose the next high-value local privacy/UX seam.

## Last synchronized
2026-09-02 — autonomous continuation, 10-minute user-update threshold, and final Android-validation policy added; Android runtime remains deferred.
