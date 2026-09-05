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
- Current branch contains verified source changes plus documentation/audit updates; the latest source fix is `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency remain CI-VERIFIED from the recorded runs.
- Legacy `BrowserUnit` whitelist import/export helpers now also resolve the active normalized profile instead of hard-coding the default profile.
- Unit Tests run `33985143542`, head `247768c4e2e442fcb9b42d299d8cf00d3c24b81b`, completed successfully; the `test` job and `Run unit tests` step both completed successfully.
- Android runtime remains intentionally deferred to final consolidated validation.

## Engineering checkpoint
The tab overview still uses the existing `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI remains PARTIAL until a complete non-breaking mutation path is ready.

The bounded privacy/security audit includes official Android cross-checks for SSL override policy, file-origin settings, cleartext traffic, and backup semantics.

The whitelist transfer path is now consistently profile-aware for both the active settings route (`ProfileScopedWhitelistTransfer`) and the legacy `BrowserUnit` import/export helpers. Bookmark import/export remains unchanged.

The download-cookie policy is enforced by both the main `BrowserUnit.download()` path and the `HelperUnit.save_as()` path; both gate the `Cookie` request header on `send_download_cookies`.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred.

## Final Android validation rule
Do not repeatedly build/install/test the APK. Complete source review, deterministic JVM tests, CI, review, and documentation first; reserve Android runtime verification for one consolidated final device-validation phase.

## Last updated
2026-09-05 — completed the bounded whitelist profile-consistency fix, reconciled the security audit with the actual active paths, confirmed the consolidated Unit Tests workflow passed, and kept Android runtime validation deferred until the final device pass.
