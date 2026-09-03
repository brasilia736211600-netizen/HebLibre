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
- Current branch HEAD is `93b1f48fe83d9b140bc53a45ec1b5aa6f72041c7`; changes after the clean code checkpoint now include the bounded source correction for profile-aware whitelist transfer, a dependency-free normalization test seam, and documentation/audit updates.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency remain CI-VERIFIED from the recorded runs.
- The profile-aware whitelist transfer correction and its new normalization tests are SOURCE-VERIFIED; CI verification for the current revision is pending.
- Android runtime remains intentionally deferred to final consolidated validation.

## Engineering checkpoint
The tab overview still uses the existing `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI remains PARTIAL until a complete non-breaking mutation path is ready.

The bounded privacy/security audit records explicit product or architecture decisions needed before touching SSL override behavior, automatic backup of `Ninja4.db`, application-level cleartext traffic, or the coupling of file-origin access with DOM storage under `sp_remote`.

The whitelist import/export path is now routed through `ProfileScopedWhitelistTransfer`, which derives the active profile and applies it to whitelist table reads and duplicate checks. A pure `normalizeProfileId(String)` seam is covered by `ProfileScopedWhitelistTransferTest`; bookmark import/export remains unchanged.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred.

## Final Android validation rule
Do not repeatedly build/install/test the APK. Complete source review, deterministic JVM tests, CI, review, and documentation first; reserve Android runtime verification for one consolidated final device-validation phase.

## Last updated
2026-09-03 — added the JVM test seam for the profile-aware whitelist transfer correction and synchronized the current HEAD. CI remains pending for this revision; no speculative security-policy changes introduced.
