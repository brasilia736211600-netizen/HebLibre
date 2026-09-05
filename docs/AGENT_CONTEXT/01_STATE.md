# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live checkpoint
`12c237e0ab9832adb509889c1d1202cd73f2e8bf` — AndroidX WebKit dependency evaluation (`1.14.0`).

## Previous verified application-source checkpoint
`247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — profile-aware whitelist import/export correction.

## Work completed in this session
- Added `ProfileMetadata`, a dependency-free reusable profile metadata contract with stable profile id ownership, name, color, icon, notes, tags, and group.
- Added JVM tests covering normalization, required fields, optional fields, duplicate/blank tag handling, and immutability.
- Evaluated AndroidX WebKit `1.14.0` as the candidate dependency baseline while preserving `minSdkVersion 21`.

## Evidence
- SOURCE-VERIFIED: profile metadata implementation and current WebKit dependency are present on `genspark-dev`.
- TEST-VERIFIED: pending current CI run for checkpoint `12c237e0ab9832adb509889c1d1202cd73f2e8bf`.
- CI-VERIFIED: pending current CI run.
- ANDROID-RUNTIME-VERIFIED: not claimed; final consolidated validation only.
- DOCUMENTED: this context plus workflow state are synchronized after the bounded implementation step.

## Architecture findings
AndroidX WebKit multi-profile support exists from WebKit `1.9.0`; `WebViewCompat.setProfile()` associates a WebView with a named Profile and `ProfileStore` manages profiles. The current application still constructs `NinjaWebView` directly, so profile binding must happen before other WebView operations. The next implementation must capability-check `WebViewFeature.MULTI_PROFILE` and avoid claiming isolation when the feature is unavailable.

`androidx.webkit:webkit:1.14.0` is the selected compatibility candidate for the current `minSdkVersion 21` baseline. WebKit `1.15.0` raises minSdk to 23; do not upgrade past 1.14.x without an explicit support-floor decision.

## Deferred policy decisions
Do not change SSL certificate override semantics, application cleartext policy, automatic backup semantics, or the coupling of file-origin access with DOM storage without an explicit product/architecture decision.

Do not fake profile-local proxying with process-global `ProxyController` behavior.

## Next executable action
After the current `1.14.0` CI run completes, add the smallest capability-checked WebView profile-binding seam around `WebViewCompat.setProfile()`/`WebViewFeature.MULTI_PROFILE`, with deterministic contract tests where possible, without yet rewriting the browser lifecycle or claiming full profile isolation.
