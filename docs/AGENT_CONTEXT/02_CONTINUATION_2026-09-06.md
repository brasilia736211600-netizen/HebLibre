# HebLibre Continuation Checkpoint — 2026-09-06

## Current branch
`genspark-dev`

## Source checkpoint at this save
`b3086e0b7c939a4c43936acba5b72e9bec132c7b` plus no production changes after that test checkpoint.

## Completed in this continuation
- Re-verified the current GitHub branch and authoritative workflow context.
- Downloaded and independently verified the latest available historical Android Runtime Smoke artifact. It is from an older source checkpoint and is explicitly not current release evidence.
- Confirmed the latest current Runtime Smoke and Unit workflow failures occur before any runner step, so no current APK was produced by those runs.
- Confirmed the Runner Probe also fails before any step, including its `ubuntu-22.04` job. This classifies the current CI blocker as runner allocation/startup, not a Gradle or emulator test failure.
- Added an atomic SQLite transaction around `RecordAction.deleteProfileRecords(...)` so all app-owned profile records/privacy-rule rows are purged as one unit.
- Detected and fully reverted an attempted JAXP/XML hardening change after compatibility review; no incompatible XML-hardening code remains on the branch.
- Added `ProfilePreferencesStore`, a compatibility bridge that stores a curated set of browser/privacy preferences in per-profile SharedPreferences namespaces while preserving the existing global-preference consumers.
- Updated `ProfileCatalogStore` so profile switching saves outgoing browser/privacy preferences, initializes the target namespace, and loads the selected profile settings into the existing global preference contract. New profiles inherit current settings on first initialization; deleted profiles clear their profile preference namespace.
- Extended `ProfileManagerSmokeActivity` with deterministic isolation checks for profile-local `desktop_mode` and `userAgent` values, including restoration and cleanup.
- Extended `ProfilePreferencesStore` with deterministic typed snapshots and safe restoration of only recognized profile-owned preference keys.
- Extended `ProfileTransferCodec` with a backward-compatible `PREFERENCES` section. New plain/AES-GCM exports can carry the typed profile-owned preference snapshot; legacy V1 exports still decode successfully with an empty preference map.
- Extended `ProfileTransferActivity` to export the active profile preference snapshot and restore it during import. Cookies, WebView storage, and login secrets remain outside the portable contract.
- Extended JVM transfer tests for plain preference round-trip, encrypted preference round-trip, tamper/wrong-password rejection, malformed encrypted dimensions, and legacy exports.
- Extended Android Runtime Smoke to verify profile preference snapshot → portable codec → restore behavior.

## Verification classification
- SOURCE-VERIFIED: yes for current source at the branch checkpoint.
- TEST-VERIFIED: current fresh hosted execution remains blocked by runner startup; deterministic JVM tests are present but have not produced fresh hosted results for this checkpoint.
- CI-VERIFIED: blocked. Current GitHub-hosted jobs fail before steps.
- ANDROID-RUNTIME-VERIFIED: historical only on older checkpoint; new preference-transfer smoke coverage has not executed on a fresh runner.
- ARTIFACT-VERIFIED: historical artifact only; no current artifact exists while runner startup is blocked.
- DOCUMENTED: yes.

## Profile-settings scope
Current profile-local browser/privacy settings are the explicitly inspected profile-relevant keys: desktop mode, screenshot protection, media-permission guard, third-party-cookie control, download-cookie behavior, images, Save-Data, HTTPS-only, Global Privacy Control, history saving, location, ad blocking, JavaScript, cookies, remote content, favorite/start URL, search engine/custom search, and user-agent override. These are persisted per profile and switched through the compatibility bridge.

UI-only preferences (theme, toolbar layout, tab presentation, gesture mappings, filter colors) remain global intentionally. They require a separate ownership decision and are not reclassified by guesswork.

Profile transfer now includes the curated browser/privacy preference snapshot in both plain and encrypted exports. WebView cookies/storage/login secrets remain excluded.

## Current material blockers
1. GitHub-hosted runner allocation/startup failure affects the build/test/smoke workflows, including a dedicated Ubuntu 22.04 runner probe.
2. A current APK cannot be downloaded from Actions until a runner executes the build.
3. WebView storage isolation beyond the supported AndroidX WebKit multi-profile capability remains bounded by platform behavior and must not be overstated.
4. Per-profile WebView proxy routing remains architectural; no global proxy emulation is allowed.
5. Full profile-local application UI preferences are intentionally deferred pending a product-level ownership decision.
6. Physical-device validation remains pending for the consolidated product scope.

## Next executable work
- Continue only bounded privacy/product hardening with deterministic seams; avoid speculative feature expansion.
- Inspect any newly produced workflow runs on `genspark-dev`; once a runner executes, immediately obtain the fresh APK artifact, verify its checksum, and run the consolidated runtime smoke gate.
- Perform final physical-device validation after the fresh CI/emulator checkpoint is green.
