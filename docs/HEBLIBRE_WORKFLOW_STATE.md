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
- Latest verified source fix: `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` (`BrowserUnit` whitelist import/export now uses the active normalized profile).
- Source checkpoint was consolidated by Unit Tests run `33985143542`; its `test` job and `Run unit tests` step completed successfully.
- Latest branch HEAD is `cdf887b02bacc6412b2545a2608db21b741ae604`; it updates the Android emulator smoke workflow only and does not modify application runtime source.
- Unit Tests run `33986682891` on the preceding source/CI checkpoint completed successfully and produced `HebLibre-debug-arm-splits`.
- Android Runtime Smoke run `33987712315` on the current HEAD completed successfully; the `runtime-smoke` job and its `Run app on Android emulator` step completed successfully.
- No unverified third-party-cookie behavior change remains.
- P2.1–P2.11, download-cookie privacy, tab reorder core, remote-content default consistency, and whitelist profile consistency are recorded as SOURCE/TEST/CI verified.

## Engineering checkpoint
The tab overview uses the existing `ScrollView` + `LinearLayout` item path; `AlbumItem` uses normal click for selection and long-click for close. Tab reorder core is implemented and tested, but the UI affordance remains partial because long-click cannot be repurposed without changing established close-tab behavior.

Whitelist transfer is consistently profile-aware in both the active task path and legacy `BrowserUnit` helpers. Bookmark transfer remains unchanged.

The download-cookie policy is enforced by both the main download path and `HelperUnit.save_as()`, with the compatibility-preserving default enabled.

The bounded security audit is complete for the current scope. SSL certificate override, application cleartext policy, automatic backup of `Ninja4.db`, and `sp_remote` file-origin/DOM-storage coupling remain explicit product or architecture decisions and were not changed opportunistically.

## APK / Android validation
- CI built both ARM debug APK splits: `app-arm64-v8a-debug.apk` and `app-armeabi-v7a-debug.apk`.
- Both APK archives passed ZIP integrity validation in the validation environment.
- The ARM64 APK contained the expected core APK entries and a readable debug certificate; the debug certificate is not a release-signing certificate.
- The Android Runtime Smoke workflow has now executed the app on a GitHub-hosted Android emulator from the current branch HEAD and completed successfully, including its emulator execution step.
- This is emulator-runtime evidence, not physical-device evidence. The user's moto g stylus 5G has not been runtime-validated in this session.

## Final validation gate
Source review, deterministic tests, CI, APK packaging, and emulator smoke validation are complete for the current bounded scope. The remaining gate for release confidence is the consolidated physical-device pass.

The intended device pass covers navigation/search, HTTPS-only/GPC/Save-Data, desktop mode, screenshot protection, media/geolocation permissions, third-party-cookie behavior, downloads including Save As, profile/whitelist transfer, tab selection/close/reorder-core behavior, settings search, and general regression paths.

If device defects appear, fix them as one consolidated batch, run CI against the resulting source checkpoint, and repeat the final device pass once.

## Deferred work
QR scanner, PWA, true tab hierarchy, multi-window, broader tracking protection, DoH, broad fingerprinting defenses, full WebRTC privacy, complete profile storage isolation, isolated tabs, per-container proxy/Tor, extensions/uBlock, on-device AI, Reader Mode, and a dedicated tab-reorder UI remain deferred by design rather than silently implemented.

## Last updated
2026-09-05 — reconciled current branch HEAD, verified emulator smoke execution on GitHub Actions, and narrowed the remaining validation gate to the physical target device.
