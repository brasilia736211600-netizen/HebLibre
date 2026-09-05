# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live source checkpoint
`40858a77b48542cdc8c173b390d0b5e9bfcd6eec` — profile-aware browsing records plus strengthened debug-only Android smoke coverage.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and profile resource strings. Production `ProfileManagerActivity` remains non-exported.
- CI: Unit Tests workflow aligned with the current Android 33/build-tools 33.0.2 baseline.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, then exercises the profile manager through a debug-only exported harness so the production profile activity does not need to be exported.
- App-owned HISTORY, BOOKAMRK, and TAB records are now profile-scoped. New databases create these tables with `PROFILE_ID`; database version 5 -> 6 migrates existing rows to `default` without dropping data; existing `RecordAction` callers remain source-compatible and automatically use the active profile for these tables.
- Debug-only smoke harness now creates a synthetic version-5 `Ninja4.db`, opens it through the current `RecordHelper` to execute the real 5 -> 6 migration, verifies legacy history/bookmark/tab preservation, then creates two profiles and proves records from one profile are invisible from the other.
- Deterministic JVM schema tests cover the new profile columns and migration SQL contract.

## Verification evidence
- SOURCE-VERIFIED: profile catalog, profile manager, WebView profile binding, profile-owned cookie path, profile-aware HISTORY/BOOKAMRK/TAB database code, migration logic, deterministic schema tests, and debug-only smoke harness are present on `genspark-dev`.
- TEST-VERIFIED: earlier Unit Tests run `33994575842` on constructor-binding checkpoint `aa727...` completed successfully. A later Unit Tests run `33995744374` for the first profile-record slice reached active execution; its final conclusion was not captured before subsequent commits, so it is not treated as evidence for the current checkpoint.
- ANDROID-RUNTIME-VERIFIED: earlier Android Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully for the browser/emulator baseline.
- ANDROID-RUNTIME-VERIFIED CURRENT SLICE: not yet claimed. The previous profile-manager smoke failure `33994913316` was diagnosed as a CI test defect: ADB attempted to start non-exported `ProfileManagerActivity` directly; browser launch and `Example Domain` checks had already passed. The workflow is now corrected with a debug-only harness, but the new final result is still pending.
- ARTIFACT-VERIFIED: a prior GitHub Actions x86_64 smoke artifact was downloaded and its APK SHA-256 matched the published checksum. Current final-checkpoint artifact verification is pending.
- DOCUMENTED: yes. This file is synchronized to the current live source checkpoint.

## Architectural boundaries
- `WebViewCompat.setProfile()` must happen before WebView use/navigation; the binder follows this ordering.
- Static `CookieManager.getInstance()` addresses the default profile; active-profile WebViews use the profile-owned CookieManager when multi-profile is available.
- Changing the active profile while an existing WebView is alive does not rebind that WebView. The current manager therefore requires browser restart to apply a new profile. Same-URL in-process switching remains a separate lifecycle feature and must not be approximated with a late `setProfile()` call.
- App-owned HISTORY/BOOKAMRK/TAB records are now profile-local at the database layer. Existing pre-profile rows are assigned to `default` during 5 -> 6 migration.
- Profile-local secure import/export, optional encryption, and per-profile proxy routing are not yet claimed complete.
- Do not change SSL certificate override semantics, cleartext policy, backup semantics, or file-origin/DOM-storage coupling without an explicit decision.
- Do not emulate profile-local proxying with process-global `ProxyController` behavior.

## Next executable slice
1. Capture fresh CI conclusions/artifacts for checkpoint `40858a77b48542cdc8c173b390d0b5e9bfcd6eec`.
2. If green, move to the next bounded P1 gap: profile-local session/tab restoration across browser restart, using the now-profile-scoped TAB storage; add deterministic tests and extend the debug-only smoke harness to prove persistence/restore without touching production export boundaries.
3. Keep same-URL switching, secure import/export/encryption, and proxy routing as separate design/tested slices.
