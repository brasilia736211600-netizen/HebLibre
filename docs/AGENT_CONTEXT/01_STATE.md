# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live source checkpoint
`1840e0c20ed94dc96b0f0d32753efa7fe6170c1d` — profile-aware app records plus profile-owned launcher session restore.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and profile resource strings. Production `ProfileManagerActivity` remains non-exported. Duplicate profile IDs are rejected in the editor.
- CI: Unit Tests workflow aligned with the current Android 33/build-tools 33.0.2 baseline.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, then exercises the profile manager through a debug-only exported harness so the production profile activity does not need to be exported.
- App-owned HISTORY, BOOKAMRK, and TAB records are profile-scoped. New databases create these tables with `PROFILE_ID`; database version 5 -> 6 migrates existing rows to `default` without dropping data; existing `RecordAction` callers remain source-compatible and automatically use the active profile for these tables.
- Debug-only smoke harness creates a synthetic version-5 `Ninja4.db`, opens it through the current `RecordHelper` to execute the real 5 -> 6 migration, verifies legacy history/bookmark/tab preservation, and proves profile records do not leak between two profiles.
- `ProfileSessionPolicy` provides deterministic URL/title/session-ownership rules with JVM tests.
- `ProfileSessionStore` persists the current browser tab set into profile-local TAB records.
- `BrowserContainer` captures the profile identity at first tab creation and uses that captured identity when persisting the session on teardown, preventing a later profile selection from receiving the old profile's tabs.
- `SessionRestoreActivity` is now the launcher trampoline. It loads the active profile's saved HTTP(S) tabs and forwards them sequentially to the existing `singleInstance` `BrowserActivity`. External `VIEW/SEND/WEB_SEARCH` entry points remain on `BrowserActivity`.
- Debug smoke seeds a default-profile session record, kills the browser, relaunches through the launcher, and verifies `Example Domain` is restored.

## Verification evidence
- SOURCE-VERIFIED: profile catalog, profile manager, WebView profile binding, profile-owned cookie path, profile-aware HISTORY/BOOKAMRK/TAB database code, migration logic, session persistence/restore code, deterministic policy tests, and debug-only smoke harness are present on `genspark-dev`.
- TEST-VERIFIED: prior Unit Tests run `33994575842` on constructor-binding checkpoint `aa727...` completed successfully. A later Unit Tests run `33996533849` on an intermediate checkpoint was active; fresh final evidence for the current checkpoint is pending.
- ANDROID-RUNTIME-VERIFIED: earlier Android Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully for the baseline browser/emulator flow.
- ANDROID-RUNTIME-VERIFIED CURRENT SLICE: pending. The previous profile-manager run `33994913316` failed in the UI assertion after successfully launching `ProfileManagerActivity`; the workflow assertion was hardened and the new smoke additionally covers launcher session restore. Fresh final conclusion for the current checkpoint is pending.
- ARTIFACT-VERIFIED: earlier GitHub Actions x86_64 smoke APK matched its published SHA-256 checksum; current checkpoint artifact verification is pending.
- DOCUMENTED: yes. This file is synchronized to the current live source checkpoint.

## Architectural boundaries
- `WebViewCompat.setProfile()` must happen before WebView use/navigation; the binder follows this ordering.
- Static `CookieManager.getInstance()` addresses the default profile; active-profile WebViews use the profile-owned CookieManager when multi-profile is available.
- Changing the active profile while an existing WebView is alive does not rebind that WebView. The current manager therefore requires browser restart to apply a new profile. Same-URL in-process switching remains a separate lifecycle feature and must not be approximated with a late `setProfile()` call.
- Session persistence is explicitly bound to the profile that created the live WebViews, so an active-profile change during that activity lifetime cannot cross-write the old session into the new profile.
- `SessionRestoreActivity` owns only the launcher path. Existing external intent filters remain on `BrowserActivity` to preserve established integration behavior.
- App-owned HISTORY/BOOKAMRK/TAB records are profile-local at the database layer. Existing pre-profile rows are assigned to `default` during 5 -> 6 migration.
- Session restoration currently persists/restores HTTP(S) tab URLs and titles. It does not claim pixel-perfect WebView navigation history, form state, or WebView-internal storage portability.
- Profile-local secure import/export, optional encryption, and per-profile proxy routing are not yet claimed complete.
- Do not change SSL certificate override semantics, cleartext policy, backup semantics, or file-origin/DOM-storage coupling without an explicit decision.
- Do not emulate profile-local proxying with process-global `ProxyController` behavior.

## Next executable slice
1. Capture fresh Unit Tests and Android Runtime Smoke conclusions/artifacts for the current checkpoint after the latest session changes.
2. When green, implement the smallest explicit P1 portability slice: profile export/import with a versioned format, explicit plain vs encrypted semantics, and tests. Keep WebView-internal cookies/storage outside the export contract unless the platform gives a safe supported path.
3. Then evaluate profile-local settings partitioning and a concrete proxy-feasibility/design record before any network-routing implementation.
