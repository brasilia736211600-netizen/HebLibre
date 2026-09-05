# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live source checkpoint
`b9539008d1650ff1a58f3e1499b5563c71670499` — profile-aware app records, profile-owned launcher session restore, and profile transfer with optional AES-GCM encryption.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and profile resource strings. Production `ProfileManagerActivity` remains non-exported. Duplicate profile IDs are rejected in the editor.
- CI: Unit Tests workflow aligned with the current Android 33/build-tools 33.0.2 baseline.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, exercises the production profile manager through a debug-only exported harness, then kills and relaunches the app and verifies the saved session URL is restored.
- App-owned HISTORY, BOOKMARK, and TAB records are profile-scoped. New databases create these tables with `PROFILE_ID`; database version 5 -> 6 migrates existing rows to `default` without dropping data; existing `RecordAction` callers remain source-compatible and automatically use the active profile for these tables.
- Debug-only smoke harness creates a synthetic version-5 `Ninja4.db`, opens it through the current `RecordHelper` to execute the real 5 -> 6 migration, verifies legacy history/bookmark/tab preservation, and proves profile records do not leak between two profiles.
- `ProfileSessionPolicy` provides deterministic URL/title/session-ownership rules with JVM tests.
- `ProfileSessionStore` persists the current browser tab set into profile-local TAB records.
- `BrowserContainer` captures the profile identity at first tab creation and uses that captured identity when persisting the session on teardown, preventing a later profile selection from receiving the old profile's tabs.
- `SessionRestoreActivity` is now the launcher trampoline. It loads the active profile's saved HTTP(S) tabs and forwards them sequentially to the existing `singleInstance` `BrowserActivity`. When a browser task already exists, it brings that task to the foreground rather than creating a second browser entry. External `VIEW/SEND/WEB_SEARCH` entry points remain on `BrowserActivity`.
- Profile transfer: `ProfileTransferCodec` provides a versioned plain format plus optional AES-GCM encrypted format, including PBKDF2 key derivation, tamper/wrong-password rejection, and deterministic JVM coverage.
- Profile transfer UI: `ProfileTransferActivity` uses Android's document picker for export/import, requires a password for encrypted exports, never persists the password, avoids exporting WebView-internal cookies/storage/login secrets, and forces a new profile id when importing into a conflicting or reserved id.

## Verification evidence
- SOURCE-VERIFIED: profile catalog, profile manager, WebView profile binding, profile-owned cookie path, profile-aware HISTORY/BOOKMARK/TAB database code, migration logic, session persistence/restore code, profile transfer codec/UI, deterministic policy tests, and debug-only smoke harness are present on `genspark-dev`.
- TEST-VERIFIED: prior Unit Tests run `33994575842` on constructor-binding checkpoint `aa727...` completed successfully. Fresh Unit Tests for the current portability checkpoint are not yet verified because the latest runs failed before any job step executed.
- CI-VERIFIED: previous successful runs established the CI toolchain and browser smoke lane. Current checkpoint runs `33997099874` (Unit Tests) and `33997099885` (Android Runtime Smoke) both failed before the first step; rerunning the Runtime Smoke job also failed before any step. No application test assertion or compiler failure was produced by those runs.
- ANDROID-RUNTIME-VERIFIED: earlier Android Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully for the baseline browser/emulator flow. A later profile-manager smoke reached and opened `ProfileManagerActivity` before its original UI assertion failed; the assertion was hardened afterward. Fresh runtime proof for the full portability checkpoint is still pending because current GitHub runner jobs fail to start.
- ARTIFACT-VERIFIED: earlier GitHub Actions x86_64 smoke APK matched its published SHA-256 checksum. A current portability APK has not been produced by a successful current run yet.
- DOCUMENTED: yes. This file is synchronized to the live source checkpoint.

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

## Current blocking evidence
- GitHub Actions currently returns `failure` for the current Unit Tests and Runtime Smoke runs before the first job step. The Runtime Smoke retry also failed before any step and produced no job logs. Treat this as an external runner/startup failure, not as application failure, until GitHub produces an actual executing job with compiler/test/runtime output.

## Next executable slice
1. Re-establish a functioning GitHub Actions runner and obtain fresh Unit Tests + Android Runtime Smoke results for `b9539008d1650ff1a58f3e1499b5563c71670499` (or a later equivalent HEAD).
2. Once CI is executing, download the x86_64 smoke APK from the successful run and verify its checksum, then use that exact artifact for the consolidated emulator validation.
3. After green evidence, evaluate profile-local settings partitioning and record a concrete proxy-feasibility/design decision before any network-routing implementation.
4. Keep same-URL in-process switching as a separate lifecycle feature; do not fake it with late WebView profile rebinding.
