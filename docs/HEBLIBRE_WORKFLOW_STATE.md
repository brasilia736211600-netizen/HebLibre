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
- Current branch contains verified source changes plus documentation/audit updates.
- Latest verified source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` (`BrowserUnit` whitelist import/export now uses the active normalized profile).
- Latest source checkpoint was consolidated by Unit Tests run `33985143542`; its `test` job and `Run unit tests` step completed successfully.
- Latest branch HEAD is `3093466d259a543383bb76fd737ff62e354e0467`; the newest commit is documentation-only and reconciles the project map for the final validation gate.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, remote-content default consistency, and whitelist profile consistency are recorded as SOURCE/TEST/CI verified.
- Android runtime remains the only remaining validation level for the current bounded scope.

## Engineering checkpoint
The tab overview uses the existing `ScrollView` + `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder core is implemented and tested, but the UI affordance remains partial because long-click cannot be repurposed without changing established close-tab behavior.

Whitelist transfer is consistently profile-aware in both the active task path and legacy `BrowserUnit` helpers. Bookmark transfer remains unchanged.

The download-cookie policy is enforced by both the main download path and `HelperUnit.save_as()`, with the compatibility-preserving default enabled.

The bounded security audit is complete for the current scope. SSL certificate override, application cleartext policy, automatic backup of `Ninja4.db`, and `sp_remote` file-origin/DOM-storage coupling remain explicit product or architecture decisions and were not changed opportunistically.

## Final validation gate
Source review, deterministic tests, CI, and documentation are complete for the current bounded scope. The remaining gate is one consolidated Android runtime validation pass. Do not repeatedly build/install during feature development. During the final pass, cover navigation and search, HTTPS-only/GPC/Save-Data, desktop mode, screenshot protection, media/geolocation permissions, third-party-cookie preference behavior, downloads including Save As, profile/whitelist transfer, tab selection/close/reorder-core behavior, settings search, and general regression paths.

If runtime defects appear, fix them as a consolidated batch, run CI again against the resulting source checkpoint, and perform one final device recheck.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, Reader Mode, and a dedicated tab-reorder UI remain deferred by design rather than silently implemented.

## Last updated
2026-09-05 — synchronized workflow state with the reconciled master map and moved the project to the final consolidated Android validation gate.
