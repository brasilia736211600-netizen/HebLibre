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
When the user says `استمر` / `continue`, do not interrupt the engineering stream with routine progress messages. Continue internally for as long as useful: investigate, verify, diagnose, prioritize, implement, test, review, reconcile CI, and fix discovered problems. If a new problem or better priority appears, handle it internally and continue from the new priority. Use parallel investigation/execution for genuinely independent work units where possible; serialize dependent branch mutations. Before any user-facing progress/update message, complete at least **10 minutes of productive project work** in the current continuation cycle whenever tool/runtime conditions permit. Do not stop artificially after one small feature, one search, one commit, or one CI submission while useful work remains.

## Final Android-validation rule
Do not build, install, or repeatedly test the Android APK after each feature. Continue source verification, deterministic JVM tests, CI verification, review, and bounded implementation first. Reserve Android build/install/runtime verification for one consolidated final device-validation phase as far as reasonably possible. During that final pass, collect runtime regressions, fix them together, and rerun device validation only as necessary.

## User-facing checkpoint rule
After the 10-minute productive-work threshold, when a user-facing update is appropriate, keep it short and useful and report exactly four things: what is completed now; any current problem/blocker (or explicitly that none exists); where the project stands overall; and exactly one next execution step. Do not send interim activity logs, discovery notices, routine CI transitions, or warnings. Do not invent a blocker.

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
| Reader Mode | **MISSING → MEDIUM** | Candidate only after source-verifying a bounded implementation. |
| QR scanner | **MISSING → MEDIUM** | Camera/scan UI and dependency decision. Lower priority than current privacy/navigation work. |
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
P2 Steps 1–6 are CI-VERIFIED. P2.7 media permission guard and P2.8 third-party cookie blocking are source/test implemented. Their feature batch at `aa5fdace59a746359870a09bfd43644c5e07aeb6` remains the CI checkpoint until its result is directly reconciled from GitHub; later documentation commits do not change feature status. Android runtime remains deferred.

## Next candidate after CI
Reader Mode remains only a candidate and must be source-verified before implementation. If it is not a small bounded seam, choose the next high-value local privacy/UX feature instead. Never reselect Desktop Mode or another feature already implemented.

## Last synchronized
2026-09-02 — continuous autonomous execution, final-device validation, and concise four-part checkpoint reporting rules reinforced.
