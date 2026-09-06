# HebLibre Product Scope — 2026-09-06

## Purpose
This is the durable product baseline and supersedes older chat-only feature lists.

## Product objective
Build a lightweight, fast, reliable Android browser combining high-value capabilities from mature social/multi-profile browsers and legitimate privacy lessons from leading anti-detect browsers. The result should emphasize profile isolation, persistent sessions, privacy, profile-aware settings, social-web productivity, diagnostics, defensive security testing, and reliable recovery while remaining local-first and low-overhead.

## Research baseline
The public GitHub Social Browser README describes multi-tabs/multi-users, autofill, ad blocking, popup/redirect blocking, safety mode, script-manager support, multi-profile UA/proxy settings, data import/export, password protection, multi-user access, password manager, shortcuts, cloud sync, downloads, video preview, translation, zoom/sound controls, PDF export/reader, page editing, proxy/UA managers, download manager, bookmarks, developer tools, session sharing, resource blocking, and planned permissions/site notes/API/RSS tooling. Its repository is proprietary, so it is a feature reference only: https://github.com/absunstar/Social-Browser-Releases

For anti-detect references, current 2026 comparisons repeatedly surface these ten products: Multilogin, GoLogin, AdsPower, Dolphin Anty, Octo Browser, Kameleo, Incogniton, MoreLogin, Undetectable, and Nstbrowser. Across these products, recurring useful patterns include reusable profiles, profile organization, persistent cookies/sessions, import/export, profile-specific settings, proxy association/status, automation/API integration, local/cloud profile storage, team/profile management, and profile consistency workflows. These patterns are requirements signals, not an instruction to reproduce detection-evasion behavior.

## HebLibre feature disposition
### Implement / extend
- Named reusable profiles.
- Profile metadata: name, color/icon, group, tags, notes.
- Fast profile switching, search, filtering, sorting.
- Explicit profile duplication/template semantics.
- Profile-local app-owned history, bookmarks, tabs, sessions and curated settings.
- WebView multi-profile binding and profile-owned CookieManager path where genuinely supported.
- Profile import/export with explicit security semantics and encrypted transfer package where defined.
- Multi-user site access through normal isolated profile/session workflows.
- Profile health and consistency diagnostics.
- Privacy & Storage status and per-profile data clearing.
- Profile-local Network & Identity settings: real WebView proxy configuration for the active profile (applied before browser startup), proxy bypass rules, curated UA presets, custom UA, and preferred language.
- Fast profile handoff through restart-aware network configuration; proxy settings are not hot-swapped into existing WebViews.
- Site permission editor.
- Measurable popup/redirect/resource controls.
- Lightweight social-web utilities: selection, downloads where permitted, previews, translation, zoom/sound, PDF/print, notes.
- Developer/page diagnostics, API testing and page analysis as testing/observability tools.
- Download manager and bookmark/history/profile transfer improvements.
- Optional local-first synchronization/export.
- Fingerprint exposure audit as read-only diagnostics.
- Local red-team test harness for systems the operator owns or is explicitly authorized to assess.
- Detection-rule test scenarios that exercise controlled combinations of browser, storage, permission, network, and behavioral signals against a test endpoint.

### Architectural before implementation
- Dynamic per-WebView/per-tab proxy switching inside one live process; AndroidX WebKit `ProxyController` is process-wide, so HebLibre intentionally applies the active profile's proxy at process startup instead of pretending it supports hot per-WebView routing.
- Automatic timezone derivation from proxy geolocation; current WebView APIs do not expose a reliable profile-local timezone override or proxy-location resolver, so no external geolocation service is silently added.
- Complete WebView disk/storage partitioning beyond currently supported APIs.
- DoH/custom resolver architecture.
- Full WebRTC privacy architecture.
- Android-compatible extension/script runtime.
- Multi-window and PWA support.
- Reader Mode with safe extraction architecture.
- Lab-only profile mutation/simulation APIs, restricted to local/authorized test environments and never exposed as a general site-evasion mechanism.

### Explicitly excluded from the general-purpose browser
- Fraud-system or anti-fraud detection bypass against third-party services.
- Identity-verification bypass or ban evasion.
- Fingerprint spoofing whose primary purpose is defeating a third-party detection system.
- Behavioral stealth/bot-detection evasion against third-party services.
- Covert credential/session theft or session sharing.

The stated purpose of a feature does not change this boundary. HebLibre can support the same security research objective through controlled red-team testing, observability, measurement, and test-environment simulation rather than shipping an operational evasion engine.

## Authorized security-testing model
HebLibre's security-testing track is divided into two clearly separated surfaces:

1. **Production browser surface:** privacy, profile isolation, diagnostics, and legitimate user controls. No operational third-party anti-fraud bypass.
2. **Authorized laboratory surface:** a local or explicitly configured test endpoint owned by the operator, with recorded test cases, controlled signal permutations, detection-rule assertions, and evidence capture. The lab can model how a detector reacts to inconsistent or manipulated signals without turning those capabilities into a portable third-party evasion feature.

The lab should report:
- Which detector rule fired.
- Which observable signal contributed to the decision.
- Expected versus observed classification.
- False positive / false negative status.
- Reproducibility metadata.
- Timestamp, profile/test-case identifier, and test artifact hash where practical.

## Educational defensive-testing capability
HebLibre may include a **Privacy & Fingerprint Exposure Audit**. It is read-only and can report browser/device surfaces observable by a site, such as WebView/platform version, UA, language, viewport, timezone/locale as exposed by the platform, storage/cookie capability, selected WebGL/canvas/audio API availability, permission state, and legitimate network/security indicators.

HebLibre may also ship a local diagnostic/test page that helps a site owner inspect these signals and validate detection rules. It must not forge or randomize them for evasion on arbitrary third-party sites.

## Priority roadmap
### P0 — release quality
Fast startup, low memory overhead, lifecycle/crash hardening, deterministic tests, GitHub-hosted emulator Smoke, consolidated APK/artifact verification, then physical-device validation only at the final coherent checkpoint.

### P1 — profile/workspace
Complete profile metadata and fast switching, profile-local curated settings/data/session boundaries, profile consistency diagnostics, secure transfer/duplication, site permission controls, network/identity controls, and site-data management.

### P1 — privacy/social web
Maintain HTTPS-only, GPC, Save-Data, tracking cleanup, screenshot protection, media/geolocation guards, cookie controls, Safe Browsing, and profile-aware whitelists; strengthen popup/redirect/resource controls with measurable tests; add lightweight social-web utilities.

### P2 — advanced privacy/developer tooling
Storage/site-data viewer and clearer, permission editor, resource diagnostics, fingerprint exposure audit, page analyzer/API tester, and optional script/resource controls only with a genuine runtime seam.

### P2 — authorized red-team laboratory
Build a local/explicitly authorized detector-test harness with scenario definitions, detector-result assertions, signal inventories, reproducible reports, and regression suites for the user's own future site/platform.

### P3 — architecture candidates
Complete WebView partitioning, dynamic per-WebView network routing where platform support permits, DoH, full WebRTC privacy, extensions, multi-window, PWA, Reader Mode, and QR/barcode features where justified.

## Lightweight engineering constraints
Prefer existing Android/WebView APIs, avoid heavy dependencies and always-on services, keep core browsing local-first, and never claim isolation/capability that is not source/runtime verified. Every material feature must document startup, memory, storage, battery, dependency and lifecycle impact.

## Validation policy
Do not repeatedly build/install APKs during feature development. Finish source review and deterministic tests, then CI, then one consolidated emulator pass. Physical-device validation comes last for the complete feature checkpoint.

## Last updated
2026-09-06 — scope refreshed from the Social Browser GitHub feature inventory, 2026 anti-detect comparisons, and AndroidX WebKit API feasibility. Profile-local proxy/UA/language support is now an implemented bounded capability; operational third-party anti-fraud bypass and fingerprint spoofing remain excluded.
