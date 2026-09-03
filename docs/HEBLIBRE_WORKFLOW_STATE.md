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
- Current branch HEAD is `60122394490ee1e199bb3f26312dd25e1c971703`; changes after the clean code checkpoint now include a small source correction for profile-aware whitelist transfer plus documentation/audit updates.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, and remote-content default consistency remain CI-VERIFIED from the recorded runs.
- The new profile-aware whitelist transfer correction is SOURCE-VERIFIED; CI verification for the current revision is pending.
- Android runtime remains intentionally deferred to final consolidated validation.

## Engineering checkpoint
The tab overview still uses the existing `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder UI remains PARTIAL until a complete non-breaking mutation path is ready.

The bounded privacy/security audit records explicit product or architecture decisions needed before touching SSL override behavior, automatic backup of `Ninja4.db`, application-level cleartext traffic, or the coupling of file-origin access with DOM storage under `sp_remote`.

The whitelist import/export path has now been corrected so its active-profile transfer entry point derives `ProfileIdentity` and applies the resulting profile id to whitelist table reads and duplicate checks. Bookmark import/export remains unchanged.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, and Reader Mode remain deferred.

## Final Android validation rule
Do not repeatedly build/install/test the APK. Complete source review, deterministic JVM tests, CI, review, and documentation first; reserve Android runtime verification for one consolidated final device-validation phase.

## Last updated
2026-09-03 — implemented the bounded whitelist import/export profile-isolation correction and recorded CI as pending for the current source revision. No speculative security-policy changes introduced.
