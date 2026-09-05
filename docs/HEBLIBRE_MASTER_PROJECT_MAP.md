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
Legacy FOSS Browser-derived Android WebView application. Gradle 5.4.1 / AGP 3.5.2, **compile SDK 33 / build-tools 33.0.2**, minSdk 21, targetSdk 29, JDK 11 for Gradle, JDK 17 for CI SDK/emulator tooling.

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
- Whitelist import/export profile-awareness — SOURCE/TEST/CI-VERIFIED; consolidated Unit evidence exists on an earlier checkpoint.
- GitHub-hosted Android emulator smoke — successful on an earlier documented checkpoint; fresh current execution is blocked by runner allocation.
- Named profile metadata/catalog, WebView multi-profile binding, profile-owned CookieManager path, and profile manager UI — SOURCE-VERIFIED on current branch.
- Profile-scoped HISTORY/BOOKMARK/TAB storage with real v5→6 migration and leakage smoke coverage — SOURCE-VERIFIED on current branch.
- Profile-owned session persistence/launcher restore with existing-browser task reuse — SOURCE-VERIFIED on current branch.
- Profile export/import using a versioned format, optional AES-GCM encryption, PBKDF2 password derivation, wrong-password/tamper rejection, document picker UI, and explicit exclusion of WebView-internal cookies/storage/login secrets — SOURCE-VERIFIED on current branch.
- Profile-transfer parser hardening: exact version-header validation plus encrypted salt/IV/ciphertext dimension checks, with deterministic malformed-input tests — SOURCE-VERIFIED.
- Profile-record import is transactional across HISTORY/BOOKMARK/TAB inserts; invalid records cannot leave partial database state, and failed imports remove the newly created profile catalog entry — SOURCE-VERIFIED with debug rollback smoke coverage.
- Database deletion hardening: profile-domain and URL deletion paths bind data values through SQLite selection arguments rather than interpolating them into SQL predicates — SOURCE-VERIFIED.
- User-profile deletion now purges app-owned HISTORY/BOOKMARK/TAB plus profile-scoped WHITELIST/JAVASCRIPT/COOKIE/REMOTE rows in one SQLite transaction before catalog deletion — SOURCE-VERIFIED with smoke coverage.
- `ProfilePreferencesStore`: curated browser/privacy settings are persisted per profile through a compatibility bridge while existing global preference consumers remain unchanged.
- `ProfileCatalogStore`: profile switches save the outgoing curated browser/privacy settings, initialize/load the incoming profile settings, and retain the existing controlled browser restart behavior.
- Profile transfer now includes the curated typed browser/privacy preference snapshot in both plain and AES-GCM formats; legacy V1 exports remain readable.
- JVM profile-transfer tests now cover plain and encrypted preference round-trips plus legacy compatibility and tamper/wrong-password rejection.
- Android smoke coverage now exercises profile preference isolation and snapshot → codec → restore behavior.
- CI diagnostic hardening: Unit and Runtime Smoke workflows pin `ubuntu-24.04`; a minimal runner probe was added to isolate hosted-runner startup from Android/toolchain execution.
- Runtime Smoke coverage includes debug-only launchers for production Profile Manager and Profile Transfer activities without exporting those production activities.

## Existing HebLibre baseline — do not reimplement
Multi-tab browsing, tab overview, Home/Bookmarks/History, search/autocomplete and configurable search engines, navigation gestures, find-in-page, PDF/print, downloads, fullscreen/video handling, JavaScript/Cookie/Remote/AdBlock controls with whitelists, Safe Browsing, bookmark import/export, custom User-Agent, clear-on-exit, and AMOLED/pure-black theme are already present.

## Current architecture boundary
Profile identity, named profile metadata/catalog, WebView profile binding, profile-owned cookie access, app-owned history/bookmarks/tabs, profile-scoped session restore, curated browser/privacy settings, and local app-owned profile transfer exist. Complete application-wide profile-local settings are **not** claimed: UI-only theme/toolbar/tab/gesture/filter preferences remain global until ownership is explicitly classified. Complete Chromium/WebView disk data-directory isolation is also not claimed. Per-profile networking is not established.

## Security and architecture decisions not to silently reopen
- Do not use process-global AndroidX WebView `ProxyController` to emulate per-profile proxy routing.
- Per-profile proxying requires a separate network-layer architecture or another capability that provides genuine profile/request isolation.
- Do not silently move UI-only theme/gesture/filter preferences into profiles without an ownership and migration decision.
- SSL certificate-error override behavior, application-level cleartext capability, automatic backup semantics for `Ninja4.db`, and the coupling of file-origin access with DOM storage under `sp_remote` remain explicit decision points.

## 2026 feature synthesis
### P0 — Release quality
Performance, startup latency, memory usage, lifecycle reliability, crash resistance, deterministic tests, CI, durable continuity, and final physical-device validation.

### P1 — Real profile isolation
Reusable named profiles; profile metadata; curated local browser/privacy settings; profile-local cookies/login state/storage where technically supported; profile-local session restoration/history/bookmarks policy; secure profile import/export; optional encrypted sensitive profile data; safe migration from the default profile.

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

## Deferred / excluded
- Primary-purpose detection bypass, fraud-system evasion, identity-verification bypass, ban evasion, or covert stealth automation.
- Large always-on services, mandatory cloud dependencies, or heavy frameworks without explicit performance justification.
- Speculative competitor parity that lacks a concrete Android seam or user-value case.

## Current phase
`P1 portability implementation → CI/runtime recovery → remaining settings/network architecture decisions`

## Current blocking items
- GitHub Actions hosted jobs continue to fail before any job step. The latest Runtime Smoke job `33999892923` on checkpoint `0ef058381decd4bb2c8698d443da0ca1c7ab54e2` reports `runner_id=0`, empty runner name, and `steps=[]`; no Android build or test executed. A separate runner probe showed the same pre-step failure on Ubuntu 22.04.
- A successful current x86_64 GitHub Actions artifact is therefore not yet available for consolidated emulator validation.
- Complete UI-only profile localization and per-profile proxy routing remain architectural/deferred and are intentionally not represented as completed features.
- Physical-device validation remains pending for the consolidated product scope.

## Current next executable step
Use the first fresh GitHub Actions capacity that successfully assigns a hosted runner: verify Unit Tests and Runtime Smoke on the latest source checkpoint, then download the exact x86_64 APK + published checksum, independently verify the artifact, and perform one consolidated emulator validation. Until that runner is available, continue only bounded source work with deterministic tests and no speculative profile/network architecture expansion.

## Android validation gate
Do not repeatedly build/install APKs during feature development. Complete source review, deterministic tests, CI, and documentation first. Then perform consolidated emulator and physical-device validation. Emulator evidence remains separate from physical-device evidence.

## Last synchronized
2026-09-06 — synchronized to the profile preference transfer/hardening wave; current branch HEAD is tracked in `docs/AGENT_CONTEXT/01_STATE.md` and `02_CONTINUATION_2026-09-06.md`.
