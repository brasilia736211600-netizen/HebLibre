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
| Tracking/query-parameter cleanup | **MISSING → EASY** | No generic tracker-parameter cleaner is present in `BrowserUnit`; current `queryWrapper()` only unwraps a small set of Google redirect forms. Candidate for first new feature. |
| Desktop mode | **MISSING → MEDIUM** | Requires a user-facing toggle and `WebSettings` UA/viewport behavior. Existing UA infrastructure can be reused. |
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
| HTTPS-only mode | **MISSING → EASY/MEDIUM** | Navigation policy can potentially be enforced locally without new architecture. Needs careful compatibility behavior. |
| DNS over HTTPS | **MISSING → ARCHITECTURAL** | Requires resolver/network integration not present in WebView architecture. |
| Global Privacy Control | **MISSING → EASY/MEDIUM** | Request-header support may be locally implementable, but WebView API limitations must be respected. |
| Fingerprinting defenses | **MISSING → ARCHITECTURAL** | Broad anti-fingerprinting changes are not justified before foundational navigation/privacy work. |
| WebRTC privacy controls | **MISSING → ARCHITECTURAL** | Requires engine-level handling not exposed by current architecture. |
| Screenshot protection | **PARTIAL/VERIFY** | Existing screenshot/fullscreen-related handling exists; exact prevention semantics need source verification before claiming parity. |
| Clear-on-exit | **MISSING → MEDIUM** | Requires lifecycle/data-clearing policy; possible without replacing the browser engine. |
| Extensions | **MISSING → ARCHITECTURAL** | Current Android WebView architecture is not a Firefox-extension runtime. Not a near-term port. |
| uBlock Origin | **MISSING → ARCHITECTURAL** | Depends on extension/engine capabilities absent here. Existing AdBlock remains separate. |
| On-device AI | **MISSING → ARCHITECTURAL** | Requires model/runtime/storage/UI architecture. Not a near-term target. |
| Translation | **MISSING → MEDIUM/ARCHITECTURAL** | Requires translation service/engine choice. Not first target. |
| PDF/Markdown/full-page export | **PARTIAL** | PDF/print already exists; Markdown/full-page export is additional functionality. |
| Custom search engines / bangs | **PARTIAL** | Configurable search engines exist; WebLibre-style bang routing is additional. |
| Download manager enhancements | **PARTIAL** | Download handling exists; external manager/advanced controls are additional. |
| Multi-window | **PARTIAL/VERIFY** | Some Android/system integration exists, but full WebLibre-style multi-window behavior needs explicit source verification. |
| Tab gestures | **ALREADY** | Gesture infrastructure already exists in HebLibre. |
| OLED/pure-black theme | **VERIFY** | Must inspect current theme resources before adding anything. |
| Global settings search | **MISSING → EASY/MEDIUM** | UI-only indexing/search over existing preferences; useful but lower priority than navigation/privacy. |
| Container/backup migration | **MISSING → MEDIUM/ARCHITECTURAL** | Depends on actual container/profile data model. |

## First implementation candidate
**Tracking/query-parameter cleanup** is the current first target because:
1. HebLibre does not currently perform generic tracking-query cleanup in its URL normalization path.
2. `BrowserUnit.queryWrapper()` already centralizes the transition from user-entered URL/query text to the URL sent to `NinjaWebView`.
3. `NinjaWebView.loadUrl()` already routes navigation through `BrowserUnit.queryWrapper()`.
4. A pure-Java cleaner can be tested independently before Android wiring.
5. The change can remain intentionally conservative: remove only a small allowlisted set of well-known tracking parameters and `utm_*`, preserving path, meaningful query parameters, and fragments.

## Explicit non-goals for the first URL-cleanup implementation
- no full URL canonicalization;
- no arbitrary parameter deletion;
- no network requests;
- no new dependency;
- no navigation-policy rewrite;
- no attempt to solve fingerprinting, cookies, WebRTC, proxy, or DNS privacy in the same change.

## Evidence anchors
Current HebLibre navigation source shows `NinjaWebView.loadUrl()` passes URLs through `BrowserUnit.queryWrapper()` before `super.loadUrl(...)`. Current `BrowserUnit` contains Google redirect unwrapping but no generic tracker-parameter filtering. Existing URL behavior is covered by a plain-JVM `BrowserUnitTest` characterization suite.

The separate WebLibre feature pool includes tracking-parameter stripping/query cleanup alongside many larger privacy, container, networking, extension, local-search, and AI capabilities. Those features remain a source pool rather than an automatic HebLibre roadmap.

## Verification boundary
This matrix is **DOCUMENTED / SOURCE-INFORMED**. It is not an Android-runtime feature verification record.
