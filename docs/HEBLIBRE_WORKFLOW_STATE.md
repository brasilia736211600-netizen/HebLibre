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
- Current branch HEAD is `bb227491f4252e91d43ad61e9ad86a8557c2c54c`; changes after the clean code checkpoint are documentation/audit only.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency remain CI-VERIFIED from the recorded runs.
- The whitelist import/export audit item is SOURCE-VERIFIED as already corrected in the active settings path by `ProfileScopedWhitelistTransfer` and task routing; no new runtime patch is required.
- Unit Tests run `33703506073` for the previous source checkpoint completed successfully; its `test` job and `Run unit tests` step both completed successfully.
- Android runtime remains intentionally deferred to final consolidated validation.

## Engineering checkpoint
The tab overview still uses the existing `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI remains PARTIAL until a complete non-breaking mutation path is ready.

The bounded privacy/security audit now includes official Android cross-checks for SSL override policy, file-origin settings, cleartext traffic, and backup semantics.

The active whitelist transfer path is profile-aware: `ExportWhiteListTask` and `ImportWhitelistTask` route whitelist tables through `ProfileScopedWhitelistTransfer`, which resolves the active `ProfileIdentity` and applies it to reads and duplicate checks. Legacy `BrowserUnit` whitelist transfer helpers remain default-profile methods but are not the active settings path; bookmark import/export remains unchanged.

A new SOURCE-VERIFIED privacy gap was identified: `HelperUnit.save_as()` creates its own `DownloadManager.Request` and unconditionally forwards the WebView cookie, bypassing the `send_download_cookies` policy already enforced by the main `BrowserUnit.download()` path. This is the next bounded runtime candidate.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred.

## Final Android validation rule
Do not repeatedly build/install/test the APK. Complete source review, deterministic JVM tests, CI, review, and documentation first; reserve Android runtime verification for one consolidated final device-validation phase.

## Last updated
2026-09-04 — synchronized workflow state with the current GitHub HEAD after reconciling the master project map and verified whitelist transfer path. Next bounded runtime candidate remains the `HelperUnit.save_as()` cookie-forwarding policy bypass.
