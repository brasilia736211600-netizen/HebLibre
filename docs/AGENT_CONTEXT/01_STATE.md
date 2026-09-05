# HebLibre Agent Context — Current State

## Repository
`brasilia736211600-netizen/HebLibre` — active branch `genspark-dev`.

## Current live checkpoint
`10028b42450974d8361da3a874e67036189fbd64` — active-profile WebView binding plus profile-local third-party cookie policy.

## Previous application-source checkpoints
- `aa7272158cc73cb8f76bc7c34ff52008087ba1a8` — profile binding wired across all `NinjaWebView` constructors.
- `7520c0326f88e1cb99764b699eeb62297b873489` — WebKit 1.9.0 with compileSdk/build-tools 33 alignment.
- `cab285b5abea743593a4f0813d9ee8f099c642a5` — active-profile WebView binding wired immediately after `super(context)`.
- `a9677287744bf80bf06626522a8e0831d04e9d9b` — capability-checked WebView profile-binding seam.
- `247768c4e2e442fcb9b42d299d8cf00d3c24b81b` — profile-aware whitelist import/export correction.

## Work completed in this session
- Added `ProfileMetadata`, a dependency-free reusable profile metadata contract with stable profile id ownership, name, color, icon, notes, tags, and group.
- Added JVM tests covering metadata normalization, required fields, optional fields, duplicate/blank tag handling, and immutability.
- Evaluated WebKit `1.14.0`; CI proved the existing legacy build toolchain could not consume that dependency cleanly because of the newer annotation-experimental dependency path.
- Reconciled the dependency spike to `androidx.webkit:webkit:1.9.0`, the first stable multi-profile release, and aligned CI/app build inputs to Android 33/33.0.2 while preserving `minSdkVersion 21` and `targetSdkVersion 29`.
- Added `WebViewProfileBindingPolicy` and `WebViewProfileBinder`. The binder checks `MULTI_PROFILE`, preserves legacy behavior for the default profile, and binds only named profiles through `WebViewCompat.setProfile()`.
- Wired profile binding immediately after every `NinjaWebView` `super(...)` constructor call so XML-created and programmatically-created WebViews are covered.
- Audited the active cookie policy against the AndroidX profile API and corrected `NinjaWebView` to use `WebViewCompat.getProfile(this).getCookieManager()` whenever multi-profile is supported. The legacy `CookieManager.getInstance()` fallback remains only for WebView implementations without `MULTI_PROFILE`.
- Upgraded Unit Tests CI to install the same Android 33/build-tools 33.0.2 baseline used by the application.
- Strengthened Android Runtime Smoke CI to build an x86_64 APK, publish a checksum, install it on an API 29 emulator, launch the app, open `https://example.com`, verify the process stays alive, verify rendered `Example Domain` UI text, and reject recent fatal-exception signatures.
- Created and synchronized this `docs/AGENT_CONTEXT/` control plane. Current-branch context is authoritative.

## Evidence
- SOURCE-VERIFIED: current profile metadata, WebKit dependency, WebView binding, profile-local cookie-policy path, CI workflows, and context files are present on `genspark-dev`.
- TEST-VERIFIED: Unit Tests run `33994575842`, head `aa7272158cc73cb8f76bc7c34ff52008087ba1a8`, completed successfully. The build, APK output checks, artifact upload, and `Run unit tests` step all passed.
- CI-VERIFIED: Unit Tests are verified for the constructor-binding checkpoint. The profile-local cookie-policy checkpoint `10028b42450974d8361da3a874e67036189fbd64` was pushed after that run and requires a new current-HEAD CI result. Android Runtime Smoke run `33994575905` for the constructor-binding checkpoint is still in progress at the emulator step.
- ANDROID-RUNTIME-VERIFIED: not yet claimed. The current emulator job is the independent smoke-validation lane, but its final result is not available yet; physical-device validation remains the final release authority.
- DOCUMENTED: yes, this file is synchronized to the latest source checkpoint.

## Architecture findings
AndroidX WebKit 1.9.0 introduced the stable WebView multi-profile API: `WebViewCompat.setProfile()` binds a WebView to a named Profile and `ProfileStore` manages profiles. Profile-specific CookieManager/WebStorage/ServiceWorker-related state is available through the Profile API, while legacy static WebView APIs such as `CookieManager.getInstance()` continue to address the default profile.

`WebViewCompat.setProfile()` must be called before other WebView use and before navigation. The current binder follows that ordering immediately after construction. The binder is capability-checked, so unsupported WebView implementations retain legacy behavior and no isolation claim is made.

The current application now applies third-party-cookie blocking through the active profile's CookieManager when `MULTI_PROFILE` is available. This removes a known cross-profile policy leak without changing the compatibility default.

The build baseline is intentionally conservative: `minSdkVersion 21`, `targetSdkVersion 29`, `compileSdkVersion 33`, WebKit 1.9.0. Do not raise targetSdk or minSdk as a side effect of profile work.

## Deferred policy decisions
Do not change SSL certificate override semantics, application cleartext policy, automatic backup semantics, or the coupling of file-origin access with DOM storage without an explicit product/architecture decision.

Do not fake profile-local proxying with process-global `ProxyController` behavior.

Do not present the current preference-backed profile identity as a complete user-facing profile manager. Named-profile creation/switcher UI, profile-local history/bookmarks/tabs, secure import/export, and same-URL switching remain incomplete.

## Next executable action
Use the newly verified profile API foundation to implement the smallest production profile catalog/selection contract: persist a normalized set of user-created profile ids and metadata, expose an explicit active-profile selection path, and ensure new WebViews are created under the selected profile. Keep same-URL switching/reload and profile-local app-owned history/bookmarks as subsequent bounded slices; do not introduce proxy routing or broad storage migration yet.
