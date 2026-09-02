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

## Remaining feature pool
| Feature | HebLibre status | Decision |
|---|---|---|
| Reader Mode | **NOT TARGETED** | Source-traced; no bounded dependency-free reader-extraction seam was established in the native WebView architecture. Do not add speculative HTML/JS injection. |
| Global settings search | **MISSING → EASY/MEDIUM** | Next candidate; source-verify bounded UI seam before implementation. |
| QR scanner | MISSING → MEDIUM | Lower priority; camera/scan UI and dependency decision. |
| PWA support | MISSING → MEDIUM | Install/launch lifecycle and manifest handling. |
| Tab hierarchy | PARTIAL → MEDIUM | Existing tabs; no parent-child model. |
| Tab stacking/advanced switcher | PARTIAL → MEDIUM | Existing overview; no stacking semantics. |
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
| Download manager enhancements | PARTIAL | Download handling exists; advanced controls remain. |
| Multi-window | PARTIAL/VERIFY | Needs explicit source verification. |

## Selection rule
Prefer the smallest high-value bounded feature with a deterministic seam. Avoid architectural gaps until demonstrated need. Do not install the Android APK during feature development; reserve device testing for the final validation phase.

## Current checkpoint
P2.1–P2.10 are CI-VERIFIED. Android runtime remains deferred. Reader Mode is formally excluded from the current P2 cycle because its source seam is not bounded; global settings search is the next candidate pending source verification.

## Last synchronized
2026-09-03 — P2.9/P2.10 evidence reconciled and Reader Mode decision closed.
