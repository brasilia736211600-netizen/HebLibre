# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Live branch checkpoint
`659f173e10671cd8d1bb2b1a0280b71c2970c40e` — latest verified branch HEAD after expanding Runtime Smoke with Profile Transfer coverage and synchronizing project documentation.

## Current source checkpoint
`ad5cec1d0c2be694c9b732ec8ba0ecb69b6ac143` — profile-aware app records, profile-owned launcher session restore, profile transfer with optional AES-GCM encryption, and SQL predicate hardening. Documentation/test-harness commits after the source change are tracked separately.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and profile resource strings. Production `ProfileManagerActivity` remains non-exported. Duplicate profile IDs are rejected in the editor.
- CI: Unit Tests workflow aligned with the current Android 33/build-tools 33.0.2 baseline and now pins `ubuntu-24.04`.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, exercises the production profile manager through a debug-only exported harness, verifies Profile Transfer opens through a separate debug-only exported harness, then kills and relaunches the app and verifies the saved session URL is restored.
- App-owned HISTORY, BOOKMARK, and TAB records are profile-scoped. New databases create these tables with `PROFILE_ID`; database version 5 -> 6 migrates existing rows to `default` without dropping data; existing `RecordAction` callers remain source-compatible and automatically use the active profile for these tables.
- Debug-only smoke harness creates a synthetic version-5 `Ninja4.db`, opens it through the current `RecordHelper` to execute the real 5 -> 6 migration, verifies legacy history/bookmark/tab preservation, and proves profile records do not leak between two profiles.
- `ProfileSessionPolicy` provides deterministic URL/title/session-ownership rules with JVM tests.
- `ProfileSessionStore` persists the current browser tab set into profile-local TAB records.
- `BrowserContainer` captures the profile identity at first tab creation and uses that captured identity when persisting the session on teardown, preventing a later profile selection from receiving the old profile's tabs.
- `SessionRestoreActivity` is now the launcher trampoline. It loads the active profile's saved HTTP(S) tabs and forwards them sequentially to the existing `singleInstance` `BrowserActivity`. When a browser task already exists, it brings that task to the foreground rather than creating a second browser entry. External `VIEW/SEND/WEB_SEARCH` entry points remain on `BrowserActivity`.
- `ProfileTransferCodec`: versioned plain format plus AES-GCM encrypted format, PBKDF2 key derivation, wrong-password/tamper rejection, and deterministic JVM coverage.
- `ProfileTransferActivity`: Android document picker, encrypted export password, password not persisted, WebView internal cookies/storage/login secrets excluded, conflicting/reserved imported IDs require a new ID.
- `RecordAction` database deletion hardening: profile-domain and URL deletion values now use SQLite selection arguments rather than string-interpolated values.
- Runtime smoke coverage: debug-only `ProfileTransferSmokeActivity` validates that the production Profile Transfer screen can be launched by an installed debug build without exporting the production activity.

## Verification evidence
- SOURCE-VERIFIED: profile catalog, profile manager, WebView profile binding, profile-owned cookie path, profile-aware HISTORY/BOOKMARK/TAB database code, migration logic, session persistence/restore code, profile transfer codec/UI, deterministic policy tests, debug-only smoke harnesses, and deletion predicate hardening are present on `genspark-dev`.
- TEST-VERIFIED: prior Unit Tests run `33994575842` on constructor-binding checkpoint `aa727...` completed successfully. Fresh current-checkpoint unit execution remains unverified because current GitHub Actions jobs terminate before executing their first step.
- CI-VERIFIED: previous successful runs established the CI toolchain and browser smoke lane. Fresh current runs remain unverified. Current hosted jobs continue to fail before any step with `runner_id=0` / empty runner assignment and no logs; no compiler/test assertion/runtime output was produced. Current Runtime Smoke attempts also produce no artifacts.
- ANDROID-RUNTIME-VERIFIED: earlier Android Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully for the baseline browser/emulator flow. A later profile-manager smoke reached and opened `ProfileManagerActivity` before its original UI assertion failed; the assertion was hardened afterward. Fresh runtime proof for the current portability checkpoint is still pending.
- ARTIFACT-VERIFIED: an earlier GitHub Actions x86_64 smoke APK matched its published SHA-256 checksum. The current Runtime Smoke lane has produced no artifact because its job never receives a runner.
- DOCUMENTED: yes. The master map and state are synchronized to the latest smoke expansion and current CI blocker.

## Architectural boundaries
- `WebViewCompat.setProfile()` must happen before WebView use/navigation; the binder follows this ordering.
- Static `CookieManager.getInstance()` addresses the default profile; active-profile WebViews use the profile-owned CookieManager when multi-profile is available.
- Changing the active profile while an existing WebView is alive does not rebind that WebView. The current manager therefore requires browser restart to apply a new profile. Same-URL in-process switching remains a separate lifecycle feature and must not be approximated with a late `setProfile()` call.
- Session persistence is explicitly bound to the profile that created the live WebViews, so an active-profile change during that activity lifetime cannot cross-write the old session into the new profile.
- `SessionRestoreActivity` owns only the launcher path. Existing external intent filters remain on `BrowserActivity` to preserve established integration behavior.
- App-owned HISTORY/BOOKMARK/TAB records are profile-local at the database layer. Existing pre-profile rows are assigned to `default` during 5 -> 6 migration.
- Session restoration currently persists/restores HTTP(S) tab URLs and titles. It does not claim pixel-perfect WebView navigation history, form state, or WebView-internal storage portability.
- Profile transfer exports only app-owned metadata/history/bookmarks/tabs. WebView-internal cookies, storage, and login secrets are explicitly outside the export contract.
- Plain export is intentionally unencrypted; encrypted export uses AES-GCM with a password-derived key and rejects wrong passwords/tampered ciphertext.
- Per-profile proxy routing, same-URL in-process switching, and complete per-profile SharedPreferences/WebView data-directory isolation are not yet claimed complete.
- Do not change SSL certificate override semantics, cleartext policy, backup semantics, or file-origin/DOM-storage coupling without an explicit decision.
- Do not emulate profile-local proxying with process-global `ProxyController` behavior.
- Complete profile-local settings require repository-wide classification and migration because production code still reads global default SharedPreferences in multiple locations; a cosmetic preference-screen namespace is insufficient.

## Current blockers
- GitHub Actions hosted jobs still fail during job startup before the first step, including the minimal runner probe. The latest probe job reports no assigned runner (`runner_id=0`) and zero executed steps. This is not application-test evidence.
- No current successful x86_64 artifact exists for consolidated emulator testing.
- Complete profile-local settings and per-profile proxy routing remain architectural work and are intentionally not represented as completed features.

## Recent durable decisions
- `D-022`: do not claim complete profile-local settings until all material readers/writers and migration semantics are covered.
- `D-023`: do not use process-global WebView `ProxyController` to emulate per-profile routing.
- `D-024`: pre-step GitHub Actions failures are classified as runner/workflow initialization failures, not application failures.
- `D-025`: parameterize database values used in deletion predicates.
- `D-026`: keep Profile Transfer production activity non-exported and reach it from Runtime Smoke only through a debug-only exported launcher.

## Current next executable slice
1. Obtain a fresh GitHub Actions run that actually assigns a hosted runner; then run Unit Tests and Runtime Smoke on the latest checkpoint.
2. On the first successful Runtime Smoke, download the exact x86_64 APK + checksum, verify the checksum, and use that artifact for one consolidated emulator validation covering browser launch, profile manager, profile transfer screen, and session restore.
3. Continue bounded profile-settings source inventory; implement only after a complete reader/writer/migration map exists and the contract is explicit.
4. Keep per-profile proxy routing deferred until a network-layer design provides genuine profile/request isolation.
5. Keep same-URL in-process profile switching separate from launcher/session restore.
