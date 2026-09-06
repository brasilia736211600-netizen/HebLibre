# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Live branch checkpoint
`20f048f61065a81ca1c4e1ab20f6fe8fd2e2e9c5` — latest documentation-synchronized branch HEAD.

## Current source checkpoint
`5982fa291b8d2988274f16217cce50c33046c0d8` — profile-aware records/session restore, profile transfer with optional AES-GCM encryption, hardened transfer parsing, transactional profile-record import, atomic profile/privacy-rule deletion, and profile-local browser/privacy preference switching + transfer coverage.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile. Profile switching saves outgoing curated browser/privacy settings and loads the incoming profile's settings through the legacy global preference contract. Deleting an active profile restores the default profile preference view immediately.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and profile resource strings. Production `ProfileManagerActivity` remains non-exported. Duplicate profile IDs are rejected in the editor.
- CI: Unit Tests workflow aligned to the Android 33/build-tools 33.0.2 baseline and pins `ubuntu-24.04`.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, exercises the production profile manager through a debug-only exported harness, verifies Profile Transfer opens through a separate debug-only exported harness, then kills and relaunches the app and verifies the saved session URL is restored.
- App-owned HISTORY, BOOKMARK, and TAB records are profile-scoped. New databases create these tables with `PROFILE_ID`; database version 5 -> 6 migrates existing rows to `default` without dropping data; existing `RecordAction` callers remain source-compatible and automatically use the active profile for these tables.
- Debug-only smoke harness creates a synthetic version-5 `Ninja4.db`, opens it through the current `RecordHelper` to execute the real 5 -> 6 migration, verifies legacy history/bookmark/tab preservation, and proves profile records do not leak between two profiles.
- `ProfileSessionPolicy` provides deterministic URL/title/session-ownership rules with JVM tests.
- `ProfileSessionStore` persists the current browser tab set into profile-local TAB records.
- `BrowserContainer` captures the profile identity at first tab creation and uses that captured identity when persisting the session on teardown, preventing a later profile selection from receiving the old profile's tabs.
- `SessionRestoreActivity` is now the launcher trampoline. It loads the active profile's saved HTTP(S) tabs and forwards them sequentially to the existing `singleInstance` `BrowserActivity`. When a browser task already exists, it brings that task to the foreground rather than creating a second browser entry. External `VIEW/SEND/WEB_SEARCH` entry points remain on `BrowserActivity`.
- `ProfileTransferCodec`: versioned plain format plus AES-GCM encrypted format, PBKDF2 password derivation, wrong-password/tamper rejection, deterministic JVM coverage, exact header validation, fixed encrypted salt/IV/ciphertext dimension validation, and backward-compatible profile preference section.
- `ProfileTransferActivity`: Android document picker, encrypted export password, password not persisted, WebView internal cookies/storage/login secrets excluded, conflicting/reserved imported IDs require a new ID. Curated profile-owned browser/privacy settings are included in plain and encrypted transfer.
- `ProfilePreferencesStore`: curated per-profile browser/privacy settings bridge with deterministic typed snapshots/restoration. Unknown or malformed imported entries are ignored. The controlled set covers browser/privacy settings explicitly inspected in the settings resources, not UI-only theme/gesture/filter options.
- `RecordAction` database deletion hardening: profile-domain and URL deletion values now use SQLite selection arguments rather than string-interpolated values.
- `RecordAction.importProfileRecords(...)`: app-owned history/bookmark/tab imports now execute in one transaction using the canonical profile-ID policy and `insertOrThrow`, so invalid input cannot leave a partial import.
- `ProfileTransferActivity`: failed profile-record import removes the newly created catalog entry; active-profile selection happens only after the record transaction succeeds.
- `RecordAction.deleteProfileRecords(...)` plus Profile Manager integration: deleting a user profile purges app-owned HISTORY/BOOKMARK/TAB and profile-scoped WHITELIST/JAVASCRIPT/COOKIE/REMOTE rows in one SQLite transaction before removing the catalog entry.
- Runtime smoke coverage: debug-only `ProfileTransferSmokeActivity` validates that the production Profile Transfer screen can be launched by an installed debug build without exporting that production activity.
- Runtime smoke coverage: `ProfileManagerSmokeActivity` verifies rollback behavior after a deliberately invalid imported record, profile-record isolation, whitelist isolation, deletion purge across all profile-scoped app-owned tables, profile-preference isolation, profile-preference transfer snapshot/restore, and active-profile deletion restoring default preferences.

## Verification evidence
- SOURCE-VERIFIED: current branch source is synchronized at `5982fa291b8d2988274f16217cce50c33046c0d8`.
- TEST-VERIFIED: fresh hosted Unit Tests for current checkpoint cannot execute because the job terminates before the first step; deterministic JVM test sources are present, but no fresh current hosted result exists.
- CI-VERIFIED: current Unit Tests run `34000987431`, Android Runtime Smoke run `34000987570`, and Runner Probe run `34000987694` all failed before any runner step (`steps: null`); this remains a GitHub runner allocation/startup blocker, not an application test result.
- ANDROID-RUNTIME-VERIFIED: earlier Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully for the baseline browser/emulator flow; new profile-preference smoke coverage has not executed on a fresh runner.
- ARTIFACT-VERIFIED: historical GitHub Actions artifact `9977732103` was downloaded through the official artifact API. The APK SHA-256 independently computed as `39d572dd7707358d3391582f838f3dcc1499e9b47810e173053138819d4fd89f`, matching the embedded checksum file. The APK is historical, not current release evidence.
- DOCUMENTED: yes. `01_STATE.md` and `02_CONTINUATION_2026-09-06.md` are synchronized to the documentation checkpoint `20f048f...` with source checkpoint `5982fa...`.

## Architectural boundaries
- `WebViewCompat.setProfile()` must happen before WebView use/navigation; the binder follows this ordering.
- Static `CookieManager.getInstance()` addresses the default profile; active-profile WebViews use the profile-owned CookieManager when multi-profile is available.
- Changing the active profile while an existing WebView is alive does not rebind that WebView. The current manager therefore requires browser restart to apply a new profile. Same-URL in-process switching remains a separate lifecycle feature and must not be approximated with a late `setProfile()` call.
- Session persistence is explicitly bound to the profile that created the live WebViews, so an active-profile change during that activity lifetime cannot cross-write the old session into the new profile.
- `SessionRestoreActivity` owns only the launcher path. Existing external intent filters remain on `BrowserActivity` to preserve established integration behavior.
- App-owned HISTORY/BOOKMARK/TAB records are profile-local at the database layer. Existing pre-profile rows are assigned to `default` during 5 -> 6 migration.
- Session restoration currently persists/restores HTTP(S) tab URLs and titles. It does not claim pixel-perfect WebView navigation history, form state, or WebView-internal storage portability.
- Profile transfer exports only app-owned metadata/history/bookmarks/tabs plus a curated typed browser/privacy preference snapshot. WebView-internal cookies, storage, and login secrets are explicitly outside the export contract.
- Plain export is intentionally unencrypted; encrypted export uses AES-GCM with a password-derived key and rejects wrong passwords/tampered ciphertext plus malformed encrypted dimensions.
- Profile-record import is atomic across HISTORY/BOOKMARK/TAB inserts; a failed insert rolls back all record writes and the caller removes the newly created catalog entry.
- User-profile deletion purges app-owned HISTORY/BOOKMARK/TAB and profile-scoped privacy-rule tables before catalog removal in one SQLite transaction; if the deleted profile was active, the global preference view is switched back to the default profile immediately. This does not claim to clear WebView-internal storage/cookies beyond the existing AndroidX profile lifecycle contract.
- Profile preference switching is a compatibility bridge: curated browser/privacy settings are stored per profile and copied into the legacy global SharedPreferences namespace when a profile becomes active. UI-only preferences remain global.
- Profile transfer carries only typed values from the curated profile-owned set; unknown or malformed imported settings are ignored.
- Per-profile proxy routing, same-URL in-process switching, and complete per-profile WebView data-directory isolation are not yet claimed complete.
- Do not change SSL certificate override semantics, cleartext policy, backup semantics, or file-origin/DOM-storage coupling without an explicit decision.
- Do not emulate profile-local proxying with process-global `ProxyController` behavior.
- Complete application-wide profile-local settings require a separate ownership/migration decision for remaining UI/gesture/filter keys; the current curated browser/privacy set is explicitly bounded.

## Current blockers
- GitHub Actions hosted jobs still fail during job startup before the first step, including minimal runner diagnostics and normal Unit/Runtime workflows.
- No current successful x86_64 artifact exists for consolidated emulator testing because no runner has executed the current workflow.
- True WebView storage isolation beyond the supported AndroidX WebKit multi-profile capability remains bounded by platform behavior and is intentionally not overclaimed.
- Per-profile proxy routing remains architectural and intentionally deferred.
- Physical-device validation remains pending for the consolidated product scope.

## Recent durable decisions
- `D-022`: do not claim complete profile-local settings until all material readers/writers and migration semantics are covered.
- `D-023`: do not use process-global WebView `ProxyController` to emulate per-profile proxy routing.
- `D-024`: pre-step GitHub Actions failures are classified as runner/workflow initialization failures, not application failures.
- `D-025`: parameterize database values used in deletion predicates.
- `D-026`: keep Profile Transfer production activity non-exported and reach it from Runtime Smoke only through a debug-only exported launcher.
- `D-027`: profile-transfer parser accepts only exact version headers and validates encrypted field dimensions before key derivation/decryption; malformed exports fail closed.
- `D-028`: profile-record imports are transactional and the catalog entry is removed on record-import failure; activation occurs only after the transaction succeeds.
- `D-029`: deleting a user profile purges its app-owned HISTORY/BOOKMARK/TAB rows before catalog deletion.
- `D-030`: profile deletion also purges all profile-scoped privacy-rule rows (WHITELIST/JAVASCRIPT/COOKIE/REMOTE); WebView-internal storage/cookies remain governed by the existing AndroidX profile lifecycle boundary.
- `D-031`: curated browser/privacy preferences use a compatibility bridge and are transferred as typed values; UI-only preferences remain global until their ownership is explicitly classified.
- `D-032`: deleting an active profile immediately restores the default profile's global preference view and requests the existing controlled restart.

## Current next executable slice
1. Obtain the first GitHub Actions capacity that actually assigns a hosted runner; verify Unit Tests and Runtime Smoke on the latest source checkpoint, then download the exact x86_64 APK + checksum and perform one consolidated emulator validation.
2. Continue only bounded profile/privacy hardening with deterministic seams; do not broaden profile-local settings by guessing ownership.
3. Keep per-profile proxy routing deferred until a network-layer design provides genuine profile/request isolation.
4. Keep same-URL in-process profile switching separate from launcher/session restore.
5. After a fresh green current runner/emulator checkpoint, perform the physical-device validation gate and only then classify the build as release-ready.
