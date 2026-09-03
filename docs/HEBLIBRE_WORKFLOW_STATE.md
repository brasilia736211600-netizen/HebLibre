# HebLibre Workflow State

## Canonical continuity
GitHub is the source of truth. Chat history, agent memory, and unstated local state are non-authoritative.
Repository: `brasilia736211600-netizen/HebLibre`
Branch: `genspark-dev`
Default branch: `l10n_crowdin`

Execution protocol:
`READ → VERIFY → RECONCILE → PLAN → EXECUTE → TEST → DIFF → REVIEW → COMMIT → SAVE STATE`

Verification levels: SOURCE-VERIFIED, TEST-VERIFIED, CI-VERIFIED, ANDROID-RUNTIME-VERIFIED, DOCUMENTED. Never conflate them.

## Current repository state
- `genspark-dev` code is anchored at the last clean verified implementation checkpoint `5fffd65e80e2a616a5273befe7cdca6309441490`.
- Current branch HEAD is `27e0c2b76b6981ec24388574aef7f103d130611a`; changes after the clean code checkpoint are documentation/audit only.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency remain CI-VERIFIED from the recorded runs.
- The whitelist import/export audit item has been SOURCE-VERIFIED as already corrected in the active settings path by `ProfileScopedWhitelistTransfer` and task routing; no new source patch is required.
- Android runtime remains intentionally deferred to final consolidated validation.

## Engineering checkpoint
The tab overview still uses the existing `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI remains PARTIAL until a complete non-breaking mutation path is ready.

The bounded privacy/security audit records explicit product or architecture decisions needed before touching SSL override behavior, automatic backup of `Ninja4.db`, application-level cleartext traffic, or the coupling of file-origin access with DOM storage under `sp_remote`.

The active whitelist transfer path is profile-aware: `ExportWhiteListTask` and `ImportWhitelistTask` route whitelist tables through `ProfileScopedWhitelistTransfer`, which resolves the active `ProfileIdentity` and applies it to reads and duplicate checks. Legacy `BrowserUnit` whitelist transfer helpers remain default-profile methods but are not the active settings path; bookmark import/export remains unchanged.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred.

## Final Android validation rule
Do not repeatedly build/install/test the APK. Complete source review, deterministic JVM tests, CI, review, and documentation first; reserve Android runtime verification for one consolidated final device-validation phase.

## Last updated
2026-09-03 — corrected the whitelist audit by tracing the active settings transfer path; no runtime source change introduced in this step.
