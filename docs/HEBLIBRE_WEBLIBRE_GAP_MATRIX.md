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

## Remaining feature pool
| Feature | HebLibre status | Decision |
|---|---|---|
| Reader Mode | **NOT TARGETED** | Source-traced; no bounded dependency-free reader-extraction seam was established in the native WebView architecture. Do not add speculative HTML/JS injection. |
| QR scanner | **SOURCE-VERIFIED / MEDIUM** | No existing QR/barcode scanner, decoder, camera permission, scanner intent, or QR-specific dependency was found in the inspected repository surface. `AndroidManifest.xml` declares no `CAMERA` permission; `app/build.gradle` has no ZXing/ML Kit/camera scanning dependency. A complete scanner therefore requires a new decoding/camera integration rather than a dependency-free local seam. Defer implementation until a concrete library/platform decision is justified. |
| PWA support | **SOURCE-VERIFIED / MEDIUM** | Existing app is a conventional Android WebView: `BrowserActivity` handles normal `http`/`https` intents and `NinjaWebViewClient` keeps web navigation inside the WebView. No Web App Manifest parsing, `WebChromeClient` install-prompt bridge, PWA install metadata, standalone launch intent, or service-worker lifecycle integration was found. A real installable/standalone PWA feature therefore needs a new manifest/install lifecycle seam; no bounded dependency-free JVM contract was established. Defer implementation until the lifecycle contract is explicitly scoped. |
| Tab hierarchy | **SOURCE-VERIFIED / MEDIUM** | `BrowserContainer` is a flat `List<AlbumController>` with index-based add/remove/get; `AlbumController` exposes only view activation/deactivation; `AlbumItem` switches/removes tabs without parent metadata. No parent/child relation, opener identity, hierarchy model, or deterministic hierarchy policy exists. Implementing true tab hierarchy would require a new tab model contract plus UI/lifecycle integration. Defer until the parent-child semantics are explicitly defined. |
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
| Multi-window | PARTIAL/VERIFY | `WebSettings.setSupportMultipleWindows(true)` and `BrowserController.onCreateView(Message)` exist, so this needs targeted source verification before any change. |

## Selection rule
Prefer the smallest high-value bounded feature with a deterministic seam. Avoid architectural gaps until demonstrated need. Do not install the Android APK during feature development; reserve device testing for the final validation phase.

## Current checkpoint
P2.1–P2.11 are CI-VERIFIED. Android runtime remains deferred. Reader Mode is formally excluded from the current P2 cycle. Global settings search is complete. QR scanner, PWA support, and tab hierarchy source verification are complete and classified MEDIUM. The next candidate is **Tab stacking/advanced switcher source verification**, unless multi-window source verification reveals a smaller bounded seam.

## Last synchronized
2026-09-03 — Tab hierarchy source verification completed; existing model is flat and lacks parent-child semantics; no implementation made; next candidate is tab stacking/advanced switcher source verification.
