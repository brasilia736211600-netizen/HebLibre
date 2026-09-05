# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live source checkpoint
`a942f276836adf00e58812e227404035ecfa2e23` — profile manager runtime smoke CI integration. This commit changes only the smoke workflow; application-source changes are in its ancestors.

## Completed bounded work
- `ProfileMetadata`: immutable profile metadata contract with id/name/color/icon/notes/tags/group plus normalization and JVM tests.
- AndroidX WebKit `1.9.0` selected as the conservative stable multi-profile baseline; `minSdkVersion 21` preserved; compile/build toolchain aligned to Android 33 / build-tools 33.0.2 while `targetSdkVersion 29` remains unchanged.
- `WebViewProfileBindingPolicy` and `WebViewProfileBinder`: capability-checked named-profile binding through `WebViewCompat.setProfile()`.
- `NinjaWebView`: active-profile binding added immediately after `super(...)` in all constructors; active profile cookie policy uses the profile's CookieManager when `MULTI_PROFILE` is supported.
- `ProfileCatalogPolicy`: deterministic normalized catalog with default profile invariant, valid id rules, serialization, selection, add/remove, and tests.
- `ProfileCatalogStore`: local SharedPreferences catalog persistence, metadata persistence, active-profile validation, and safe fallback to the default profile.
- Profile manager UI: Settings entry, catalog/editor/delete/select activity, manifest registration, and localized resource strings.
- CI: Unit Tests workflow aligned with the current Android 33/build-tools 33.0.2 baseline.
- CI: Android Runtime Smoke builds an x86_64 APK, publishes a checksum, installs on an API 29 emulator, launches the browser and `https://example.com`, checks `Example Domain`, opens `ProfileManagerActivity`, verifies `Profiles` and `Default`, then rejects recent fatal-exception signatures.

## Verification evidence
- SOURCE-VERIFIED: application profile foundation, profile manager, cookie isolation path, and CI workflows are present on `genspark-dev`.
- TEST-VERIFIED: earlier Unit Tests run `33994575842` on constructor-binding checkpoint `aa727...` completed successfully, including APK build/output verification and unit tests.
- ANDROID-RUNTIME-VERIFIED: Android Runtime Smoke run `33994758706` on checkpoint `5aa5a6e87e45b0fa3cc7aca820c1de6b8bfbdb8e` completed successfully. Every setup/build/upload/emulator step passed.
- ARTIFACT-VERIFIED: downloaded GitHub Actions artifact `HebLibre-x86_64-smoke-apk.zip`, extracted APK, and independently verified that the APK SHA-256 matched its published checksum: `39d572dd7707358d3391582f838f3dcc1499e9b47810e173053138819d4fd89f`.
- CURRENT CI: run `33994913316` (Android Runtime Smoke) and run `33994913329` (Unit Tests) target current source checkpoint `a942f276836adf00e58812e227404035ecfa2e23`; both are active, with Smoke building the x86_64 APK and Unit Tests building ARM splits. Their final conclusions are not yet claimed.
- DOCUMENTED: yes. This file is synchronized to the current source checkpoint and verification state.

## Architectural boundaries
- `WebViewCompat.setProfile()` must happen before WebView use/navigation; the binder follows this ordering.
- Static `CookieManager.getInstance()` addresses the default profile; active-profile WebViews use the profile-owned CookieManager when multi-profile is available.
- Changing the active profile while an existing WebView is alive does not rebind that WebView. The current manager therefore requires browser restart to apply a new profile. Same-URL in-process switching remains a separate lifecycle feature and must not be approximated with a late `setProfile()` call.
- Profile-local app-owned history, bookmarks, tabs/session restoration, secure import/export, optional encryption, and per-profile proxy routing are not yet claimed complete.
- Do not change SSL certificate override semantics, cleartext policy, backup semantics, or file-origin/DOM-storage coupling without an explicit decision.
- Do not emulate profile-local proxying with process-global `ProxyController` behavior.

## Next executable slice
After the current `a942...` CI finishes, implement only the next bounded P1 profile-isolation slice supported by the existing architecture: make app-owned history/bookmark/session records profile-aware with a backward-compatible migration and deterministic tests. Keep same-URL switching, secure import/export/encryption, and proxy routing as separate design/tested slices.
