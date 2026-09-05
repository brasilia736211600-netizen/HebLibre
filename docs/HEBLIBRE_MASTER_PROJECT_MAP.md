# HebLibre Master Project Map

## Authority
- Repository: `brasilia736211600-netizen/HebLibre`
- Active branch: `genspark-dev`
- Default branch: `l10n_crowdin`
- GitHub is the source of truth.
- Chat history, model memory, plugin memory, and unstated local state are non-authoritative.

## Durable control plane
The repository `docs/` area is the persistent continuity/control plane. A new agent must inspect all current project-control documents before planning work. Documentation is not proof; live source and GitHub evidence override stale notes.

## Canonical workflow
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

## Verification ladder
`SOURCE-VERIFIED → TEST-VERIFIED → CI-VERIFIED → ANDROID-RUNTIME-VERIFIED → DOCUMENTED`

Do not substitute one evidence level for another.

## Active product direction
The active product direction is defined by `docs/HEBLIBRE_PRODUCT_SCOPE_2026-09-06.md`.

HebLibre is being evolved into a lightweight, fast, reliable Android browser that combines high-value profile-management and privacy patterns observed in mature social/multi-profile browsers with legitimate privacy lessons from leading anti-detect browsers.

The active interpretation of anti-detect is privacy, profile isolation, session continuity, configuration consistency, and user-controlled data separation. Functionality whose primary purpose is bypassing fraud/security detection, identity verification, bans, or platform enforcement is excluded.

Older chat-only feature lists are superseded by the active product-scope document.

## Continuity requirements
Every material step must be recoverable from GitHub without chat context. Persist:
- exact live HEAD;
- source checkpoint;
- current task;
- material reasoning/decision;
- changed and intentionally unchanged files;
- tests and CI evidence;
- blockers/unresolved decisions;
- one next executable step;
- Android validation status.

Do not commit trivial thoughts, but do commit material decisions, rejected approaches, architecture conclusions, scope changes, blockers, test conclusions, and milestones before they can be lost.

## Baseline toolchain
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, compile SDK 29, build-tools 28.0.3, JDK 11 for Gradle, JDK 17 for CI SDK tooling.

## Completed engineering
- Build/toolchain recovery, minimal JUnit4 harness, CI workflow recovery.
- P1 profile/identity groundwork.
- P2.1 tracking/query cleanup — CI-VERIFIED.
- P2.2 HTTPS-only — CI-VERIFIED.
- P2.3 Global Privacy Control — CI-VERIFIED.
- P2.4 Desktop Mode — CI-VERIFIED.
- P2.5 Screenshot Protection — CI-VERIFIED.
- P2.6 built-in search bangs — CI-VERIFIED.
- P2.7 bounded WebView camera/microphone permission guard — CI-VERIFIED.
- P2.8 optional third-party cookie blocking — CI-VERIFIED with compatibility-preserving default.
- P2.9 geolocation privacy guard — CI-VERIFIED.
- P2.10 Save-Data contract/fallback correction — CI-VERIFIED.
- P2.11 global settings search — CI-VERIFIED.
- Download cookie privacy control — SOURCE/TEST/CI-VERIFIED.
- BrowserContainer tab reorder core + identity tests — SOURCE/TEST/CI-VERIFIED.
- Remote-content default consistency — SOURCE/TEST/CI-VERIFIED.
- Whitelist import/export profile-awareness — SOURCE/TEST/CI-VERIFIED; latest source fix `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`; consolidated Unit Tests run `33985143542` passed.
- GitHub-hosted Android emulator smoke — successful on checkpoint `48300a4ad2c366e2987cea9949de8e722089c962`; emulator evidence only.
- Current-HEAD Unit Tests run `33990897505` passed.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Current architecture boundary
Profile-aware whitelist persistence and profile identity exist. SharedPreferences, CookieManager, Chromium WebView disk storage, history, and bookmarks are not yet fully partitioned per profile. Per-profile networking is not yet established. The current application remains a legacy Android WebView architecture.

## Security decisions not to silently reopen
SSL certificate-error override behavior, application-level cleartext capability, automatic backup semantics for `Ninja4.db`, and the coupling of file-origin access with DOM storage under `sp_remote` remain explicit decision points.

## 2026 feature synthesis
### P0 — Release quality
Performance, startup latency, memory usage, lifecycle reliability, crash resistance, deterministic tests, CI, durable continuity, and final physical-device validation.

### P1 — Real profile isolation
Reusable named profiles; profile metadata; local settings; profile-local cookies/login state/storage where technically supported; profile-local session restoration/history/bookmarks policy; secure profile import/export; optional encrypted sensitive profile data; safe migration from the default profile.

### P1 — Profile networking and privacy
Per-profile proxy configuration where Android/WebView permits it; proxy status/validation; profile-local language/locale/User-Agent controls where technically valid; cross-profile leakage prevention; explicit UI showing which state is isolated.

### P1 — Privacy baseline
Maintain existing tracking cleanup, HTTPS-only, GPC, Save-Data, screenshot protection, media/geolocation guards, cookie control, whitelists, Safe Browsing, and conservative WebView defaults. Expand popup/redirect/resource controls only where measurable and testable.

### P2 — Profile productivity
Fast profile switcher; profile search/filter/sort; safe duplicate-profile semantics; session restore controls; profile-aware transfer tools; lightweight download improvements; translation utilities where cost-effective; tab grouping/reordering UI without breaking close semantics.

### P2 — Advanced privacy controls
Per-profile storage diagnostics; site-data viewer/clearer; permission editor; resource-type controls; optional script/resource controls with performance limits; privacy status page with evidence levels.

### P3 — Architectural candidates
Complete WebView storage partitioning, per-profile network routing, DoH, full WebRTC privacy architecture, Android-compatible extension runtime, multi-window, PWA, QR/barcode scanning, and a safe Reader Mode architecture. These require explicit design checkpoints before implementation.

## Competitor feature-source synthesis
Social Browser's public GitHub release repository lists multi-profile operation, proxy configuration, data import/export, password protection, multi-user site access, password management, autofill, cloud sync, translation, zoom/sound control, PDF export, page editing, proxy/User-Agent management, download management, developer tools, session sharing, and resource blocking. These are treated as feature references only because the project is proprietary. urlSource: Social Browser GitHub READMEhttps://github.com/absunstar/Social-Browser-Releases/blob/main/README.md

Current 2026 market references repeatedly identify Multilogin, GoLogin, AdsPower, Dolphin Anty, Kameleo, Octo Browser, Incogniton, and other profile-oriented tools as leading products. Recurring high-value patterns include reusable isolated profiles, proxy association, profile metadata/organization, cookies/session handling, local/cloud storage options, synchronization, and automation APIs. These references inform product requirements but do not automatically justify implementation in a lightweight Android client.

## Deferred / excluded
- Primary-purpose detection bypass, fraud-system evasion, identity-verification bypass, ban evasion, or covert stealth automation.
- Large always-on services, mandatory cloud dependencies, or heavy frameworks without explicit performance justification.
- Speculative competitor parity that lacks a concrete Android seam or user-value case.

## Current phase
`2026 Product Scope Reset → P0/P1 architecture and bounded implementation planning`

## Current next executable step
Perform a bounded source audit against the new product scope, prioritize the smallest profile-isolation and performance seams, and create TDD-first implementation slices. Do not start large anti-detect or networking architecture changes until their Android feasibility, memory cost, and data-isolation contract are documented.

## Android validation gate
Do not repeatedly build/install APKs during feature development. Complete source review, deterministic tests, CI, and documentation first. Then perform consolidated physical-device validation. Emulator evidence remains separate from physical-device evidence.

## Last synchronized
2026-09-06 — replaced the old chat-accumulated feature backlog with the new durable 2026 product direction, strengthened continuity requirements, and recorded the legitimate-privacy boundary for anti-detect-inspired features.
